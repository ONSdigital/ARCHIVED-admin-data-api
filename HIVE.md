# Hive (Hortonworks Sandbox VM) Setup

To reduce complications with the install/setup of Hive/Hadoop etc, we will be using the Hortonworks Sandbox VM.

1. Install VirtualBox (https://www.virtualbox.org/wiki/Downloads)
2. Download the VM (https://hortonworks.com/downloads/#sandbox)
3. Import the VM into VirtualBox, use default settings, but use at least 8GB of RAM, preferably 10GB.
4. Run the VM

Once the VM is running, you should be able to go to `localhost:8888` to see the dashboard.

## Getting Company House data into Hive

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