## Barista - Chef Java Client ![semver 2.0.0 compliant](http://img.shields.io/badge/semver-2.0.0-brightgreen.svg?style=flat-square)

Java 8 [Chef](https://www.chef.io/chef/) Java Client written using [Restify](https://github.com/inversoft/restify) REST Client

I've only added methods that I'm using, open an issue if you'd like a method added, or feel free to submit a pull request, the pattern should be very easy to follow.

### Code Examples
```java
// Construct a new Chef Client
ChefClient client = new ChefClient(
    "vandelay",                      // userId used in header X-Ops-UserId
    "https://chef.acme.com",         // Server URL
    "vandelayIndustries",            // Organization Name
    "/usr/local/acme/vandelay.pem"); // Path to PEM file

// Delete a Node by Name
ClientResponse<Void, Void> response = client.deleteNode("node-name");

// Delete a Client by Name
ClientResponse<Void, Void> response = client.deleteClient("client-name");

// Retrieve All Nodes
ClientResponse<Nodes, Void> response = client.getNodes();

// Retrieve a Node by Name, print the node name if successful, else print the status code.
ClientResponse<Node, Void> response = client.getNode("fooBar");
if (response.wasSuccessful()) {
  System.out.println(response.successResponse.name); // --> fooBar
} else {
  System.out.println("Request failed. Status code : [" + response.status + "]");
}
```

## Downloading
Released versions can be downloaded from the Savant Repository.
 
 * http://savant.inversoft.org/com/inversoft/barista/
 
### Building
**Note:** This project uses the Savant build tool. To compile using using Savant, follow these instructions:

```bash
$ mkdir ~/savant
$ cd ~/savant
$ wget http://savant.inversoft.org/org/savantbuild/savant-core/1.0.0/savant-1.0.0.tar.gz
$ tar xvfz savant-1.0.0.tar.gz
$ ln -s ./savant-1.0.0 current
$ export PATH=$PATH:~/savant/current/bin/
```

Then, perform an integration build of the project by running:
```bash
$ sb int
```

For more information, checkout [savantbuild.org](http://savantbuild.org/).
