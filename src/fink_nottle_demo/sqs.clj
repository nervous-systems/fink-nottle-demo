(ns fink-nottle-demo.sqs
  (:require [fink-nottle.sqs :as sqs]
            [fink-nottle.sqs.channeled :as sqs.channeled]
            [fink-nottle.sqs.tagged :as sqs.tagged]
            [clojure.core.async :as async :refer [>!]]
            [glossop :refer [<?! <? go-catching]]
            [clojure.edn :as edn]))

(defmethod sqs.tagged/message-in  :edn [_ body] (edn/read-string body))
(defmethod sqs.tagged/message-out :edn [_ body] (pr-str body))

(defn send-loop! [creds queue-url]
  (let [{:keys [in-chan]} (sqs.channeled/batching-sends creds queue-url)]
    (go-catching
      (loop [i 0]
        (println "Sending message" i)
        (>! in-chan {:body {:event :increment :value i}
                     :fink-nottle/tag :edn})
        (<? (async/timeout (rand-int 300)))
        (recur (inc i))))))

(defn receive-loop! [creds queue-url]
  (let [message-chan (sqs.channeled/receive! creds queue-url)
        {delete-chan :in-chan} (sqs.channeled/batching-deletes creds queue-url)]
    (go-catching
      (loop []
        (let [{:keys [body attrs] :as message} (<? message-chan)
              deleting (zero? (rand-int 2))]
          (println {:body body :attrs attrs :deleting deleting})
          (when deleting
            (>! delete-chan message))
          (recur))))))

(defn configure-queue! [creds queue-name dlq-name]
  (go-catching
    (let [dlq-url   (<? (sqs/create-queue! creds dlq-name))
          dlq-arn   (<? (sqs/queue-arn!    creds dlq-url))
          queue-url (<? (sqs/create-queue! creds queue-name))]
      (<? (sqs/set-queue-attribute!
           creds queue-url
           :redrive-policy
           {:max-receive-count 2
            :dead-letter-target-arn dlq-arn}))
      queue-url)))

(defn read-creds []
  {:access-key (get (System/getenv) "AWS_ACCESS_KEY")
   :secret-key (get (System/getenv) "AWS_SECRET_KEY")})

;; Block in the entrypoints

(defn send-demo []
  (let [creds     (read-creds)
        queue-url (sqs/create-queue!! creds "fink-nottle-demo")]
    (<?! (send-loop! creds queue-url))))

(defn receive-demo []
  (let [creds     (read-creds)
        queue-url (<?! (configure-queue!
                        creds "fink-nottle-demo" "fink-nottle-demo-dead"))]
    (<?! (receive-loop! creds queue-url))))

(defn dead-letter-receive-demo []
  (let [creds     (read-creds)
        queue-url (sqs/create-queue!! creds "fink-nottle-demo-dead")]
    (<?! (receive-loop! creds queue-url))))
