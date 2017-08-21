# HBase Setup

To install HBase, run the following command:

```shell
brew install hbase
```

Use the following commands to start/stop HBase:

```shell
start-hbase.sh
stop-hbase.sh
```

## Inserting CompanyHouse/VAT/PAYE CSV data into HBase

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