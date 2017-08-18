# sbr-admin-data-api
An API for use by sbr-api for accessing CompanyHouse/VAT/PAYE data

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)]() [![Dependency Status](https://www.versioneye.com/user/projects/596f195e6725bd0027f25e93/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/596f195e6725bd0027f25e93)

## API Endpoints

If you do not specify a period, the record for the most recent period will be returned.

| method | endpoint                       | example                    |
|--------|--------------------------------|----------------------------|
| GET    | /v1/companies/${companyNumber} | GET /v1/companies/AB123456 |
| GET    | /v1/vats/${vatReference}       | GET /v1/vats/123456789012  |
| GET    | /v1/payes/${payeReference}     | GET /v1/payes/12345678     |

If you want to specify a particular period, use the format below.

| method | endpoint                                         | example                               |
|--------|--------------------------------------------------|---------------------------------------|
| GET    | /v1/periods/${period}/companies/${companyNumber} | /v1/periods/201706/companies/AB123456 |
| GET    | /v1/periods/${period}/vats/${vatRef}             | /v1/periods/201706/vats/123456789012  |
| GET    | /v1/periods/${period}/payes/${payeRef}           | /v1/periods/201706/payes/12345        |

## Environment Setup

* Java 8 or higher (https://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_jdk.html)
* SBT (http://www.scala-sbt.org/)

```shell
brew install sbt
```

## Running

With the minimal environment setup described above (just Java 8 and SBT), the sbr-admin-data-api will only work with the csv file or in-memory HBase. Further instructions for Hbase (not in memory), Hive and Impala setup/installations can be found [below](#source-setup).

To run the `sbr-ch-data-api`, run the following:

``` shell
sbt "api/run -Dsource=hbaseInMemory -Dsbr.hbase.inmemory=true"
```

Note: When using In-Memory HBase, make sure the path to the project path does not include spaces.

The environment variable `sbr.hbase.inmemory` only needs to passed in when running `source=hbaseInMemory`.

Swap the `hbaseInMemory` argument with any of the values in the table below:

| -Dsource value | Data Access                                                                                     |
|----------------|-------------------------------------------------------------------------------------------------|
| csv            | Local CSV files `./conf/sample/...` (Real CompanyHouse data, test VAT/PAYE data)                |
| hiveLocal      | Hive which runs inside the Hortonworks VM                                                       |
| hbaseLocal     | A local HBase installation                                                                      |
| hbaseInMemory  | In memory HBase tables with the CompanyHouse/VAT/PAYE CSVs loaded in                            |

## Source Setup

#### csv - No setup required

#### hiveLocal - [Hive (Hortonworks Sandbox VM) Setup](HIVE.md)

#### hbaseLocal - [HBase Setup](HBASE.md)

#### hbaseInMemory - No setup required

## Assembly

To assemble the code + all dependancies into a fat .jar, run the following:

```shell
sbt assembly
```

## Deployment

After running the following command:
 
```shell
sbt clean compile "project api" universal:packageBin
```

A .zip file is created here, `/target/universal/sbr-admin-data-api.zip`, which is pushed to CloudFoundry. 

The executable inside the .zip is configured to run with default environment variables passed in, as defined in the [build.sbt](https://github.com/ONSdigital/sbr-admin-data-api/blob/feature/hbase-in-memory/build.sbt#L85).

## Testing

## Contributing

See [CONTRIBUTING](CONTRIBUTING.md) for details.

## License

Copyright ©‎ 2017, Office for National Statistics (https://www.ons.gov.uk)

Released under MIT license, see [LICENSE](LICENSE) for details.
