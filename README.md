# VICINITY Kura Adapter
### Eclipse Kura: Introduction
Eclipse Kura is an Eclipse IoT project that provides a platform for building IoT gateways. It is a smart application container that enables remote management of such gateways and provides a wide range of APIs for allowing you to write and deploy your own IoT application. This open source Java and OSGi (Open Services Gateway initiative)-based framework simplify the design, deployment and remote management of embedded applications.

Kura runs on top of the Java Virtual Machine (JVM) and leverages OSGi, a dynamic component system for Java, to simplify the process of writing reusable software building blocks. It offers API access to the hardware interfaces of IoT Gateways (serial ports, GPS, watchdog, GPIOs, I2C, etc.). It also offers OSGI bundle to simplify the management of network configurations, the communication with IoT servers, and the remote management of the gateway.

### Kura Adapter Deployment and Configuration
In order to use this Kura Adapter, you should firstly setup your eclipse IDE, clone and import this repository. The following link, https://github.com/vicinityh2020/Vicinity-Adapter-Kura-UNIKL/wiki/Vicinity-kura-developer's-page could help you. Secondly Eclipse Kura should be installed on your Raspberry Pi. Here, https://eclipse.github.io/kura/intro/raspberry-pi-quick-start.html there are the steps to follow. 

After connecting Raspberry with eclipse Kura, export three bundles and save them. The "org.unikl.adapter.integrator" bundle is the main bundle, which is responsible to create VICINITY Adapter interface. The next one, "org.unikl.adapter.VicinityObjectInterface" makes object accessible. The third bundle, "org.unikl.adapter.philipshue" is a chosen example of using VICINITY Kura Adapter to control the Philips Hue lamps. That means, you can use this Adapter also for different purposes like controlling the temperature using temperature sensor, etc. 

Additionally, you need to download five following bundles: jackson-jaxrs-json-provider-2.4.5.jar, jackson-annotations-2.4.0.jar, jackson-core-2.4.5.jar, jackson-databind-2.4.5.jar and jackson-jaxrs-base-2.4.5.jar.

Next step is to import all eight bundles in Raspberry-framework and then let them start.

NOTE: In “org.unikl.adapter.philipshue”-codes, you should write the “lastIpAddress” that match the IP address of the Hue Bridge on your network.

### Functionality and API
