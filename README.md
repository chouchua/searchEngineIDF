# searchEngineIDF
Reads a file and creates a search engine by indexing the file.
### Assumptions
- Index is mutable.
- Capital letters ignored.
- Data can fit in memory. Index is stored in memory.

###Key Features:
- Inverted Index
- Build Index for Query.
- Query takes m log m, where m is the average number of documents containing term.
- tf-idf score is used to find relevance.

###Procedures:
- Under Run Configurations in Eclipse, you can add the file name as an argument OR simply change the file variable
- Run main.java
- Make sure file is in the working directory.

