## SQS

Try running these three simultaneously, and stopping `send-demo` after it has sent a dozen or so messages:

```
lein run -m fink-nottle-demo.sqs/receive-demo
lein run -m fink-nottle-demo.sqs/send-demo
lein run -m fink-nottle-demo.sqs/dead-letter-receive-demo
```
