# sbr-ch-api
An API for use by sbr-api for accessing Company House data

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)]()

## API Endpoints

Table of endpoints

## Environment Setup

### SBT

### Hive & Hadoop

Install Hadoop first (this takes a while) before installing Hive.

```shell
brew install hadoop
brew install hive
```

Useful link:
https://noobergeek.wordpress.com/2013/11/09/simplest-way-to-install-and-configure-hive-for-mac-osx-lion/

## Running

To run the `sbr-ch-api`, run the following:

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

Released under MIT license, see [LICENSE](LICENSE.md) for details.
