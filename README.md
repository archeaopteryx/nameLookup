Automated lookup of chemical names read from an excel file where the name cell also contains information that isn't usefult (e.g. the supplier name). Returns the identified chemical name and CAS number. 

An excel workbook used as a database is first checked. If there is no entry for the cell contents, then a website search is used. Results from the website are added to the database workbook.

In addition to look-ups, the GUI can also be used to add or update information in the database, and to update the database location. The 'database' is initialized at start up if it doesn't aready exist.
