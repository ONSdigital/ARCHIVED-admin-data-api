# sbr-admin-data-api
An API for use by sbr-api for accessing Company House data

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)]() [![Dependency Status](https://www.versioneye.com/user/projects/596f195e6725bd0027f25e93/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/596f195e6725bd0027f25e93)

## API Endpoints

| method | endpoint                     | example                  |
|--------|------------------------------|--------------------------|
| GET    | /v1/company/${companyNumber} | GET /v1/company/AB123456 |

## Environment Setup

* Java 8 or higher (https://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_jdk.html)
* SBT (http://www.scala-sbt.org/)

```shell
brew install sbt
```

## Running

With the minimal environment setup described above (just Java 8 and SBT), the sbr-admin-data-api will only work with csv file, not Hbase or Hive. Further instructions for Hbase/Hive installation can be found below.

To run the `sbr-ch-data-api`, run the following:

``` shell
sbt api/run -Dsource=csv
```

Swap the `csv` argument with any of the values in the table below:

| -Dsource value | Data Access                                                                                     |
|----------------|-------------------------------------------------------------------------------------------------|
| csv            | Local [CSV file](./conf/sample/company_house_data.csv) (first 10,000 rows of CompanyHouse data) |
| hiveLocal      | Hive which runs inside the Hortonworks VM (setup described above)                               |
| hbaseLocal     | A local hbase installation (not in a VM)                                                        |
| hiveCloudera   | Hive which runs on Cloudera (requires Kerboros setup etc.)                                      |

## Hortonworks Sandbox VM Setup

To reduce complications with the install/setup of Hive/Hadoop etc, we will be using the Hortonworks Sandbox VM.

1. Install VirtualBox (https://www.virtualbox.org/wiki/Downloads)
2. Download the VM (https://hortonworks.com/downloads/#sandbox)
3. Import the VM into VirtualBox, use default settings, but use at least 8GB of RAM, preferably 10GB.
4. Run the VM

Once the VM is running, you should be able to go to `localhost:8888` to see the dashboard.

### Getting Company House data into Hive

1. Download the Company House data (http://download.companieshouse.gov.uk/en_output.html)
2. Unzip it
3. Replace the header with 'Clean CSV Headers' section from the [CH Readme](CH.md)
4. Follow the instructions [here](https://hortonworks.com/hadoop-tutorial/how-to-use-hcatalog-basic-pig-hive-commands/#download-example-data), use the table definition from the [CH Readme](CH.md)
5. Test it works using the following query:

```SQL
SELECT * FROM company_house;
```

6. Create a table using the Parquet data format (this will speed up many queries):

```SQL
set hive.execution.engine=mr;
CREATE TABLE ch STORED AS PARQUET AS SELECT * FROM company_house;
```

Creating the Parquet table may take some time.

## Hbase Setup

## Assembly

To assemble the code + all dependancies into a fat .jar, run the following:

```shell
sbt assembly
```

## Testing

## Contributing

See [CONTRIBUTING](CONTRIBUTING.md) for details.

## License

Copyright ©‎ 2017, Office for National Statistics (https://www.ons.gov.uk)

Released under MIT license, see [LICENSE](LICENSE) for details.
