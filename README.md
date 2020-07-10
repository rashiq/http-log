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

If you run it with `./monitor.sh --log-file ./access.txt --alert-threshold 10 --alert-window 2` 
it will raise an alert if it registeres more than 10 requests/second on average for a time frame of 2 minutes.

</br>
![ezgif-6-5d6bcbe44a3f](https://user-images.githubusercontent.com/1624385/87150051-a31fed80-c2b1-11ea-8039-d0d4596dffc2.gif)
