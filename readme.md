#API Bulk Importer for WSO2 API Manager
API Bulk Importer allows you to bulk import API's to an API Manager deployment. This tool can be used in conjuction with the bulk API Export feature that I have written previously
https://github.com/nadeesha5814/APIBulkExport

##Prerequisites
Java JDK 1.8
WSO2 API Manager 1.9 or higher 
API Import/Export web-app deployed on the above API Manager instance 
Apache Maven

#What API's would this tool consume
The tool consumes the following API
1. Export/Import API - Version 1.0.1

##Steps to run the bulk exporter.

1.Edit Configuration - Open the config.properties file and change the configuration based on your own setup. Keep the Export/Import API as it is if you are not sure on which version to use. Make sure that you have all your exported API's in a single folder and also make sure that there are no other zip files in the same folder.

2.Build the project - Build the project by running mvn clean package. 

3.Run the bulk importer - Go to the target folder and run the bulk importer by executing `java -jar APIBulkImport-1.0-SNAPSHOT.jar`. You will notice that a copy of your configuration file is created in the target folder for your convience. You can edit this file as required and run the `java -jar` command to see the changes get effected. Please also note that any changes made to the configuration file in the target folder will be over written whenever you rebuild the code, hence it is encouraged to do any configuration changes to the main configuration file.

https://nadeesha678.wordpress.com/2016/03/10/bulk-importing-of-apis-to-api-manager/
