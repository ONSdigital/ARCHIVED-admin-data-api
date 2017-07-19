# sbr-ch-data-api
An API for use by sbr-api for accessing Company House data

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)]() [![Dependency Status](https://www.versioneye.com/user/projects/596f195e6725bd0027f25e93/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/596f195e6725bd0027f25e93)

## API Endpoints

| method | endpoint             | parameters         | example                     |
|--------|----------------------|--------------------|-----------------------------|
| GET    | /v1/company?id=${id} | id: company_number | GET /v1/company?id=AB123456 |

## Environment Setup

* Java 8 or higher (https://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_jdk.html)
* SBT (http://www.scala-sbt.org/)

```shell
brew install sbt
```

### Hive & Hadoop

Install Hadoop first (this takes a while) before installing Hive.

```shell
brew install hadoop
brew install hive
```

Useful link:
https://noobergeek.wordpress.com/2013/11/09/simplest-way-to-install-and-configure-hive-for-mac-osx-lion/

## Running

To run the `sbr-ch-data-api`, run the following:

``` shell
sbt api/run -Denvironment=local
```

## Assembly

To assemble the code + all dependancies into a fat .jar, run the following:

```shell
sbt assembly
```

## Contributing

See [CONTRIBUTING](CONTRIBUTING.md) for details.

## License

Copyright ©‎ 2017, Office for National Statistics (https://www.ons.gov.uk)

Released under MIT license, see [LICENSE](LICENSE) for details.
