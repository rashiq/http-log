## http-log

This command line tool watches a [CLF HTTP access log](https://en.wikipedia.org/wiki/Common_Log_Format) and generates useful statistics about it.


```
./monitor.sh -h

Usage: application [OPTIONS]

Options:
  --log-file TEXT        Log file to monitor
  --alert-threshold INT  Alert threshold for requests per second
  --alert-window INT     Alert window for threshold
  -h, --help             Show this message and exit
```

if you run it with `./monitor.sh --log-file ./access.txt --alert-threshold 10 --alert-window 2` 
it will raise an alert if it registeres more than 10 requests/second on average in for a time frame of 2 minutes.
