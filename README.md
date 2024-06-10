# Producer_Consumer_Management_System (Using gRPC)

This project implements a Producer-Consumer Management System using gRPC with Java. We utilized BloomRPC for testing and checking the requests.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [Contributing](#contributing)

## Overview

The Producer-Consumer Management System is a classic synchronization problem, where producers generate data and consumers process it. This project leverages gRPC to facilitate efficient communication between producers and consumers, ensuring that data is transmitted and processed reliably.

## Features

- **gRPC Communication:** Efficient and scalable communication between producers and consumers.
- **Multi-threading:** Supports multiple producers and consumers.
- **Request Validation:** Uses BloomRPC for testing and validating requests.
- **Error Handling:** Robust error handling and logging.

## Technologies Used

- **Java:** Programming language used for implementation.
- **gRPC:** Framework for handling remote procedure calls.
- **BloomRPC:** Tool for testing and validating gRPC requests.

## Setup and Installation

- Install dependencies:

```bash
mvn install

```

- Compile the project:
 
```bash

mvn compile

```

- Run the server:

```bash

mvn exec:java -Dexec.mainClass="com.yourpackage.Server"

```
- Run the client:
  
```bash

mvn exec:java -Dexec.mainClass="com.yourpackage.Server"

```


### Prerequisites

Ensure you have the following installed on your machine:

- Java (JDK 8 or higher)
- Maven
- BloomRPC

### Steps

1. **Clone the repository:**
   ```sh
   git clone https://github.com/your-username/Producer_Consumer_Management_System.git
   cd Producer_Consumer_Management_System

