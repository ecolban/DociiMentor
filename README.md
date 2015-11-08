DociiMentor
===========

DociiMentor is a tool to make the information at [IEEE Mentor](https://mentor.ieee.org/) available locally on your computer. It also downloads documents from IEEE Mentor and stores them locally. It allows you to enter own notes for each document

To use, create a directory, e.g., IEEE, henceforth called *start-dir*, and place the [jar](jar/DociiMentor.jar?raw=true) file in that directory. Double-click on the jar file to start the application. 

The first time you start the application, you will be prompted for the directory where you want downloaded files to be stored. A configuration file is created and stored at *start-dir*/.dociimentor/dociiconfig.xml. THe configuration file can be manually updated. Read the comments in the configuration file to learn how to configure the application. In particular, you need to list the IEEE 802 working groups and document groups you want DociiMentor to manage.

Use the pull-down menus to select a WG, document group, year, and press the Synchronize button. This will make the document meta data on IEEE Mentor available in the tool. Synchronization may take some time because it involves downloading several pages from IEEE Mentor. The left pane (document pane) contains a view of all documents that match a WG, doc group and year for which meta data has been synchronized. By clicking on a document in the left pane, the tool will display title, author and author affiliation, time of upload, your own notes, which you may insert via in the tool, etc. 

A view may be further narrowed down, by clicking the search button, and providing search criteria. Searches support SQL wild cards. 

Double-clicking on a document will open that document, or, if already open, bring into focus the window containing the document. If the document has not yet been downloaded to your computer, the tool will download it. You can also download all documents in a view by clicking the download button. 

Download of multiple files and synchronization may be aborted at any time by pressing the octogonal stop button, which lights up during synchronization and downloads. 

The export button exports a view and associated meta data to an Excel spreadsheet.