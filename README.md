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

<br/>

![Screenshot 2020-07-12 at 05 02 54](https://user-images.githubusercontent.com/1624385/87237970-04a0a300-c3fd-11ea-8b04-5bfd691f41cc.png)

<img src="https://a.rashiq.me/log-gh.png" width="0px" height="0px" style="display:none;"/>
