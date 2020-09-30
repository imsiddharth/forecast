
| **CI Provider**  | **Status** | **Time Taken(Approx)** |
| ------------- | ------------- |  ------------- |
| **Travis** | [![Travis](https://travis-ci.org/imsiddharth/forecast.svg)](https://travis-ci.org/imsiddharth/forecast) | 10+ sec [Travis Status](http://scribu.net/travis-stats/#imsiddharth/forecast/master)|


## Intro 
Imagine you have a commandline app to show tomorrow's forecast using public API: https://www.metaweather.com/api/

Sample output:
```
$ forecast dubai

Tomorrow (2019/05/01) in Dubai:
Clear
Temp: 26.5 °C
Wind: 7.6 mph
Humidity: 61%
```

## Task
* Write 1-2 automated tests.
* Ideally, tests should not touch the real service and work without the Internet.
* Create CI pipeline with GitHub Actions or any alternative.
