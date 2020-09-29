
# GPSocialBackend
> GPSocial Backend is API for Web and IOS Application
## Prerequisites

* SpringBoot v2.3.2 or later
* Gradle 6.4.1 or later
* IP2Location™ LITE IP-COUNTRY-REGION-CITY-LATITUDE-LONGITUDE Database

## Installation

Clone the repository and enter its respective folder
Ensure the database `IP2LOCATION-LITE-DB11.IPV6.BIN` have in the folder `GPSocialBackend/data/`.

If this database doesn't exist, you can access [ip2location](https://lite.ip2location.com/database/ip-country-region-city-latitude-longitude-zipcode-timezone) and download IP2LOCATION-LITE-DB11.IPV6.BIN at "IPv6 BIN".
```
├── GPSocialBackend
│   ├── data
│   │   └── IP2LOCATION-LITE-DB11.IPV6.BIN
```

## Usage

```
cd GPSBackend
./runservice
```

Server started on port : 8765 (http)

###### Check out a demo

<a href="https://asciinema.org/a/361649" target="_blank"><img src="https://asciinema.org/a/361649.svg" /></a>


## License

[MIT](https://github.com/hcnhatnam/GPSocial/blob/master/LICENSE) license.

Copyright (c) 2020 GPSocial Team
