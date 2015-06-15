(defproject io.nervous/fink-nottle-demo "0.1.0-SNAPSHOT"
  :description "fink-nottle demo project"
  :url "https://github.com/nervous-systems/fink-nottle-demo"
  :license {:name "Unlicense" :url "http://unlicense.org/UNLICENSE"}
  :aot [fink-nottle-demo.sqs]
  :source-paths ["src"]
  :dependencies [[org.clojure/clojure       "1.6.0"]
                 [org.clojure/core.async    "0.1.346.0-17112a-alpha"]

                 [io.nervous/glossop     "0.1.0"]
                 [io.nervous/fink-nottle "0.1.0-SNAPSHOT"]]
  :exclusions [[org.clojure/clojure]])
