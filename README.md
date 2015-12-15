Appetite
========

[PAUSED DEVELOPMENT - 12/14/2015]

Appetite is an Android application designed to work in a manner similar to Songza. That is, based on a perceived 
mood provided by the user, Appetite queries up local venues that align with the user's feelings. The application 
was written as a submission to the Carolina Creates Entrepreneurial competition where Appetite placed 8th.

Venue searching works underneath via the Locu API, recently acquired by GoDaddy, which searches for restaurant menu
items, descriptions, etc. for certain keywords in realtime. Each mood offered to the user for selection is tied to
a selection of keywords in the backend (hosted on RedHat OpenShift) to perform this search.

An equivalent [iOS application](https://github.com/morganhowell95/Appetite) is currently being developed, but progress 
is limited now that the competition is over and other projects are being worked on by both myself and my partner.

Server
------

The backend of the application is hosted on [OpenShift](http://site-cater.rhcloud.com/), which is responsible for
user creation/handling and Locu queries (of which our application was granted commercial access). Queries are subsequently
saved and paged for fast response, though Locu itself proved much to slow for a desirable response time, also leading
to the pausing of development.

Progress:
--------

* Query searches (based on user location)
* User creation
* Facebook/Google Integration
* Detailed Venue Information
* Travel/Contact Information

<p align="center">
<img src="https://github.com/jrpotter/appetite/blob/master/rsrc/icon.png">
</p>
