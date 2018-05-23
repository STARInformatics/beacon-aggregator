# swagger-java-client

## Requirements

Building the API client library requires [Maven](https://maven.apache.org/) to be installed.

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn deploy
```

Refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-java-client</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "io.swagger:swagger-java-client:1.0.0"
```

### Others

At first generate the JAR by executing:

    mvn package

Then manually install the following JARs:

* target/swagger-java-client-1.0.0.jar
* target/lib/*.jar

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

import bio.knowledge.client.*;
import bio.knowledge.client.auth.*;
import bio.knowledge.client.model.*;
import bio.knowledge.client.api.ConceptsApi;

import java.io.File;
import java.util.*;

public class ConceptsApiExample {

    public static void main(String[] args) {
        
        ConceptsApi apiInstance = new ConceptsApi();
        String conceptId = "conceptId_example"; // String | (url-encoded) CURIE identifier of concept of interest
        try {
            List<BeaconConceptWithDetails> result = apiInstance.getConceptDetails(conceptId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ConceptsApi#getConceptDetails");
            e.printStackTrace();
        }
    }
}

```

## Documentation for API Endpoints

All URIs are relative to *https://rkb.ncats.io/*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*ConceptsApi* | [**getConceptDetails**](docs/ConceptsApi.md#getConceptDetails) | **GET** /concepts/{conceptId} | 
*ConceptsApi* | [**getConcepts**](docs/ConceptsApi.md#getConcepts) | **GET** /concepts | 
*ConceptsApi* | [**getExactMatchesToConceptList**](docs/ConceptsApi.md#getExactMatchesToConceptList) | **GET** /exactmatches | 
*MetadataApi* | [**getConceptCategories**](docs/MetadataApi.md#getConceptCategories) | **GET** /categories | 
*MetadataApi* | [**getKnowledgeMap**](docs/MetadataApi.md#getKnowledgeMap) | **GET** /kmap | 
*MetadataApi* | [**getPredicates**](docs/MetadataApi.md#getPredicates) | **GET** /predicates | 
*StatementsApi* | [**getEvidence**](docs/StatementsApi.md#getEvidence) | **GET** /evidence/{statementId} | 
*StatementsApi* | [**getStatements**](docs/StatementsApi.md#getStatements) | **GET** /statements | 


## Documentation for Models

 - [BeaconAnnotation](docs/BeaconAnnotation.md)
 - [BeaconConcept](docs/BeaconConcept.md)
 - [BeaconConceptCategory](docs/BeaconConceptCategory.md)
 - [BeaconConceptDetail](docs/BeaconConceptDetail.md)
 - [BeaconConceptWithDetails](docs/BeaconConceptWithDetails.md)
 - [BeaconKnowledgeMapObject](docs/BeaconKnowledgeMapObject.md)
 - [BeaconKnowledgeMapPredicate](docs/BeaconKnowledgeMapPredicate.md)
 - [BeaconKnowledgeMapStatement](docs/BeaconKnowledgeMapStatement.md)
 - [BeaconKnowledgeMapSubject](docs/BeaconKnowledgeMapSubject.md)
 - [BeaconPredicate](docs/BeaconPredicate.md)
 - [BeaconStatement](docs/BeaconStatement.md)
 - [BeaconStatementObject](docs/BeaconStatementObject.md)
 - [BeaconStatementPredicate](docs/BeaconStatementPredicate.md)
 - [BeaconStatementSubject](docs/BeaconStatementSubject.md)
 - [ExactMatchResponse](docs/ExactMatchResponse.md)


## Documentation for Authorization

All endpoints do not require authorization.
Authentication schemes defined for the API:

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author

richard@starinformatics.com

