These demos read AWS credentials from `$AWS_SECRET_KEY` and `$AWS_ACCESS_KEY`.

## SQS

Try running these three simultaneously, and stopping `send-demo` after a minute or so:

lein run -m fink-nottle-demo.sqs/receive-demo
lein run -m fink-nottle-demo.sqs/send-demo
lein run -m fink-nottle-demo.sqs/dead-letter-receive-demo
