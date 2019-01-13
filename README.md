## EM Dates

EM Dates is an early modern calendar conversion service for the sixteenth- to eighteenth-century under development by the [Cultures of Knowledge][1] project at Oxford University. It is the second of what will eventually become [three Linked Open Data resources][2] also comprising [EM Places][1] and EM People built on a [shared humanities infrastructure platform][3] in collaboration with the [Humanities Cluster][20] of the Royal Dutch Academy of Arts and Sciences (KNAW) in Amsterdam.

## Goals

EM Dates is designed to provide a locale and period aware resource for individual and bulk early modern date conversions. To accomplish this, EM Dates draws on data collected in [EM Places][1] for the date(s) at which a historical place entity (e.g. the 'Republic of Venice') transitioned from one calendrical system to another (e.g. Julian to Gregorian). Given an [ISO 8601][5] formatted date and an gazetteer authority reference to either a known historical place entity (in EM Places) or to a name (e.g. in [GeoNames][6]) associated with a known place entity in [EM Places][1], it will infer the appropriate calendar and offer to make the suggested date conversion. In addition, it provides support for parsing early modern dates expessed in [Roman nomenclature][18] into ISO 8601. 

The resource will offer a web form for converting single dates as well as bulk conversion facility for uploading metadata on a series of dates and locations in a tabular file format (Excel, CSV), and receiving back from the application, also in tabular format, the converted dates alongside the initial inputs. As part of this workflow, EM Dates will provide an online review stage, where a user can examine EM Dates' inferred confidence level for each suggested conversion. The review stage will provide an opportunity for a user to revise and re-process (e.g. incomplete) input data and a means to override the suggested conversion before final export. 

Access to the tool for individual and bulk conversions will also be provided via an open [Timbuctoo][3] API. Because EM Dates stores a list of known-correct Romdan date variants to normalised ISO-8601 transformations, adding an [OpenRefine][17] endpoint for the reconciliation and/or sharing this data with the [GODOT][15] calendar gazetteer is also under active consideration.

## Status
**December 2018**: Draft conversion API and Roman parser [available for testing][19] via Docker.

**November 2018**: Rewrite of conversion algorithms on the basis of [Rheingold & Derschowitz][16]

**October 2018**: Draft Roman date parser (i.e. Julian or Gregorian dates in Roman nomenclature)

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
[15]: https://godot.date/home
[16]: https://www.cambridge.org/core/books/calendrical-calculations/B897CA3260110348F1F7D906B8D9480D#
[17]: http://openrefine.org
[18]: https://en.wikipedia.org/wiki/Roman_calendar
[19]: https://github.com/culturesofknowledge/emdates/tree/master/dev
[20]: https://huc.knaw.nl
