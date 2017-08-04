# sbr-ch-data-api
An API for use by sbr-api for accessing Company House data

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)]() [![Dependency Status](https://www.versioneye.com/user/projects/596f195e6725bd0027f25e93/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/596f195e6725bd0027f25e93)

## API Endpoints

| method | endpoint                                   | parameters                    | example                                |
|--------|--------------------------------------------|-------------------------------|----------------------------------------|
| GET    | /v1/company?companyNumber=${companyNumber} | companyNumber: company_number | GET /v1/company?companyNumber=AB123456 |

## Environment Setup

* Java 8 or higher (https://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_jdk.html)
* SBT (http://www.scala-sbt.org/)

```shell
brew install sbt
```

### Getting Company House data into Hive

To reduce complications with installing & configuring Hadoop and Hive, we use a Hortonworks Sandbox in VirtualBox, which we load the Company House data into.

1. Download and install VirtualBox (https://www.virtualbox.org/wiki/Downloads)
2. Download the Hortonworks Hadoop Sandbox for VirtualBox (https://hortonworks.com/downloads/#sandbox)

Useful links:

http://download.companieshouse.gov.uk/en_output.html

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
