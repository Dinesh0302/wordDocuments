# Word Tag Cloud Service

Build a word tag cloud service that processes documents, counts word frequencies, and provides various APIs for document analysis.

## Features

- **Document Upload**: Upload one or multiple documents (PDF, DOCX, etc.).
- **Word Frequency Analysis**: Analyze word frequencies across documents.
- **Sorted Word Counts**: Fetch word counts sorted by count and then by word.
- **Nearby Words**: Retrieve words near a given word along with their counts.

## Technologies Used

- [Spring Boot](https://spring.io/projects/spring-boot): Java-based framework for building web applications.
- [Apache POI](https://poi.apache.org/): Library for reading and writing Microsoft Office formats.
- [Redis](https://redis.io/): In-memory data structure store used for caching word counts.
- [Maven](https://maven.apache.org/): Dependency management and build tool.


## API Endpoints

Open a web browser and go to [http://localhost:8080]

Document Upload
  - POST /documents/add: Upload one or multiple documents.
   
Word Counts
  - GET /documents/word-counts: Get word counts across all documents.
    
All Words
  - GET /documents/allwords: Get word counts for each word.
  - 
Nearby Words
  - GET /documents/nearby-words/{word}: Get words near the specified word.

    
## Setup

Provide instructions on setting up the project locally. Include details on dependencies, configuration files, and any other necessary steps.

```bash
# Clone the repository
git clone https://github.com/your-username/word-tag-cloud-service.git

# Navigate to the project directory
cd word-tag-cloud-service

# Build and run the application
mvn spring-boot:run

## Authors
- Dinesh Suvarna
- dineshsuvarna0302@gmail.com

