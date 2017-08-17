# sbr-admin-data-api
An API for use by sbr-api for accessing CompanyHouse/VAT/PAYE data

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)]() [![Dependency Status](https://www.versioneye.com/user/projects/596f195e6725bd0027f25e93/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/596f195e6725bd0027f25e93)

## API Endpoints

If you do not specify a period, the record for the most recent period will be returned.

| method | endpoint                       | example                  |
|--------|--------------------------------|--------------------------|
| GET    | /v1/companies/${companyNumber} | GET /v1/company/AB123456 |
| GET    | /v1/vats/${vatReference}       | GET /v1/vat/123456789012 |
| GET    | /v1/payes/${payeReference}     | GET /v1/paye/12345678    |

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

With the minimal environment setup described above (just Java 8 and SBT), the sbr-admin-data-api will only work with csv file, not Hbase or Hive. Further instructions for Hbase/Hive installation can be found below.

To run the `sbr-ch-data-api`, run the following:

``` shell
sbt "api/run -Dsource=hbaseInMemory -Dsbr.hbase.inmemory=true"
```

Swap the `csv` argument with any of the values in the table below:

| -Dsource value | Data Access                                                                                     |
|----------------|-------------------------------------------------------------------------------------------------|
| csv            | Local CSV files `./conf/sample/...` (Real CompanyHouse data, test VAT/PAYE data)                |
| hiveLocal      | Hive which runs inside the Hortonworks VM (setup described above)                               |
| hbaseLocal     | A local HBase installation (not in a VM)                                                        |
| hbaseInMemory  | In memory HBase tables with the CompanyHouse/VAT/PAYE CSVs loaded in                            |

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

## HBase Setup

To install HBase, run the following command:

```shell
brew install hbase
```

Use the following commands to start/stop HBase:

```shell
start-hbase.sh
stop-hbase.sh
```

### Inserting CompanyHouse data into HBase

Firstly, start HBase and open the shell, then create the namespace/table for all data:

```shell
start-hbase.sh
hbase shell
create_namespace 'sbr_local_db'
create 'sbr_local_db:ch', 'd'
create 'sbr_local_db:vat', 'd'
create 'sbr_local_db:paye', 'd'
```

Create some folders for use by the [sbr-hbase-connector](https://github.com/ONSdigital/sbr-hbase-connector) and change its permissions:

```shell
cd /
sudo mkdir -p user/<username>/hbase-staging
sudo chmod 777 user/<username>/hbase-staging
```

Download the CompanyHouse data from [here](http://download.companieshouse.gov.uk/en_output.html).

In the same directory as the CompanyHouse CSV file, the VAT/PAYE fake csv data (can be found in `conf/sample/...`) and the `sbr-hbase-connector-1.0-SNAPSHOT-distribution.jar`, run the following commands:

```shell
java -cp sbr-hbase-connector-1.0-SNAPSHOT-distribution.jar uk.gov.ons.sbr.data.hbase.load.BulkLoader CH 201706 BasicCompanyDataAsOneFile-2017-07-01BasicCompanyDataAsOneFile-2017-07-01.csv
java -cp sbr-hbase-connector-1.0-SNAPSHOT-distribution.jar uk.gov.ons.sbr.data.hbase.load.BulkLoader VAT 201706 vat_data.csv
java -cp sbr-hbase-connector-1.0-SNAPSHOT-distribution.jar uk.gov.ons.sbr.data.hbase.load.BulkLoader PAYE 201706 paye_data.csv
```

The command for CompanyHouse will take ~10 minutes, a few errors will appear relating to invalid characters.

Test it works:

```shell
hbase shell
get 'sbr_local_db:ch', '201706~08209948'
```

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
