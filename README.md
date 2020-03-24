<img src="https://raw.githubusercontent.com/DevLeoko/Decision-Critical-Bayesian-Networks/master/frontend/src/assets/LogoDark.svg?sanitize=true" width="40%"/>

# Decision Critical Bayesian Networks

> Application for the identification of decision-critical situations through dynamic bayesian networks.

This project is in its core a Progressive Web App used for modeling dynamic bayesian networks.
The backend also integrates with the Frauenhofers IOSB servers through ActiveMQ to serve inference calculations for the modeled networks.\
The main features are:

- **User System** with two privilege groups
- **Network editor** to model dynamic bayesian networks
- **Network playground** to experiment with the networks
- **Evidence editor** to define expressions converting metadata to evidence used by the networks
- Backend integration with ActiveMQ to calculate inference for given metadata

## Repo Structure

### Docs

The documentation folder contains:

- Protocols of the weekly meetings
- The project specifications
- Any other non-code-related files

### Frontend

The frontend is a PWA build with **Vue**, TypeScript, Vuetify, and visNetwork.\
_Using npm for dependency management._

### Backend

The backend is for most parts a REST-Service build using **Spring Boot** and [AMiDST](http://www.amidsttoolbox.com/) for inference.\
_Using maven for dependency management._
