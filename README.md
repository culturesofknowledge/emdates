## EM Dates

EM Dates is an early modern calendar conversion service for the sixteenth- to eighteenth-century under development by the [Cultures of Knowledge][1] project at Oxford University. It is the second of what will eventually become [three Linked Open Data resources][2] also comprising [EM Places][1] and EM People built on a [shared humanities infrastructure platform][3] in collaboration with the [Huygens Institute][4] (KNAW) in Amsterdam.

## Goals

EM Dates is designed to provide a locale aware resource for individual and bulk early modern date conversions. To accomplish this, EM Dates will draw on data collected in [EM Places][1] for the date(s) at which a historical entity (e.g. the 'Republic of Venice') transitioned from one calendrical system to another (e.g. Julian to Gregorian). Given a [ISO 8601 formatted][5] date and and an authority reference to either a known historical place entity (in EM Places) or to a place name (in [GeoNames][6]) associated with a known entity in [EM Places][1], EM Dates is able to infer the appropriate calendar and offer to make the suggested date conversion. The initial release of EM Dates will provide support for conversion to the Gregorian from the Julian and Roman calendars. 

The tool will offer a web form for converting single dates as well as bulk conversion facility for uploading metadata on a series of dates and locations in a tabular file format (Excel, CSV), and receiving back from the application, also in tabular format, the converted dates alongside the initial inputs. As part of this workflow, EM Dates will provide an online review stage, where a user can examine EM Dates' inferred confidence level for each suggested conversion. The review stage will provide an opportunity for a user to revise and re-process (e.g. incomplete) input data and a means to override the suggested conversion before final export. Access to the tool for individual and bulk conversions will also be provided via an open [Timbuctoo][3] API. 

## Status
**August 2018**: Draft high-level description of feature set; initial release of minimal viable (standalone) API

## Feedback and Comments

We are keen to get your comments and feedback on EM Dates. Please get in touch by contacting Arno Bosse (Digital Project Manager, [Cultures of Knowledge][12]) by email [arno.bosse@history.ox.ac.uk][13] via [@kintopp][14] on Twitter or by creating a new GitHub issue in the repository with your comment/question.

## Team

Arno Bosse (Oxford - Project Management), Howard Hotson (Oxford - Director), Graham Klyne (Oxford – Data Modelling), Miranda Lewis (Oxford - Editor), Martijn Maas (Huygens – Systems Development), Glauco Mantegari (Design Consultant), Jauco Noordzij (Huygens – Systems Development), Marnix van Berchum (Huygens - Project Management), Mat Wilcoxson (Oxford – Systems Development).

## Acknowledgements

EM Places, EM People, and EM Dates are [funded by a grant][2] to the University of Oxford from the Andrew W. Mellon Foundation.

[1]: https://github.com/culturesofknowledge/emplaces
[2]:  http://www.culturesofknowledge.org/?p=8455
[3]:  https://github.com/HuygensING/timbuctoo
[4]:  https://www.huygens.knaw.nl/?lang=en
[5]: https://en.wikipedia.org/wiki/ISO_8601
[6]: https://geonames.org
[12]: http://culturesofknowledge.org
[13]: mailto:arno.bosse@history.ox.ac.uk
[14]: http://twitter.com/kintopp
