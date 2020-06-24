Instructions of Use:

NOTE: This is a tool for testing purposes only

1) on Jupiter Classic QAW1 select a batch of items to migrate to QAWS (for simulating migration from Llandaff to Cardiff)  - preferably the item numbers are in sequence. Keep a note of all these media item ids.
2) make a keep decision > offline archive
3) make sure that they have been archived by monitoring http://qa64w1app07.jupiter.bbc.co.uk/jupGUI/jobs/, and checking on the GUI that they have a status: online + offline
4) on your local terminal make sure you have runMigrationRunner.jar and run:-  java -jar runMigrationRunner.jar [all the item ids as arguments delimited by spaces]
5) script will prompt user for username and p/w for the QAW1, before performing DB updates to each of those items' two iteminstanceids (20 and 114, representing deep archived instances) regarding
- replacing the '/' with spaces from their storeinstanceids in the mediaiteminstanceda tables.
- setting the 'archivemigrationhighwatermark' value of the keyvaluepair table so that the migration script knows where to start migrating from.
6) after displaying the logs of the DB operations this script will then prompt user for the ssh credentials for the JOE server zgbwcjvjoe7ws01.jupiter.bbc.co.uk, so that it can create the mock files on /var/joe, used to identify each item during migration
7) After the script is finished running, ssh onto http://zgbwcjvjoe7ws01.jupiter.bbc.co.uk:/opt/bbc/jupiter/cardiff-migration-job-creator and run the migration script: java -Dspring.config.location=etc/application.yml -jar ./lib/cardiff-migration-job-creator-0.1.2.jar, which will kick off the processing of the items one by one, and can be monitored at http://rdir.qaws.jupiter.bbc.co.uk/jupiter/qaws/jupGUI/jobs/

