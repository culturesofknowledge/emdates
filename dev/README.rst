Lobsang
=======

Lobsang deals with Julian / Gregorian calendar issues given specific geographic
locations and the Time at which they switched between using either calendar
system.

Nomen est omen
--------------
* https://wiki.lspace.org/mediawiki/Lobsang_Ludd
* https://wiki.lspace.org/mediawiki/Time

Background hints
~~~~~~~~~~~~~~~~
On start-of-year:

* https://en.wikipedia.org/wiki/Old_Style_and_New_Style_dates
* https://en.wikipedia.org/wiki/Calendar_(New_Style)_Act_1750
* https://en.wikipedia.org/wiki/Julian_calendar#New_Year's_Day

Building and running emdates
----------------------------
To build and run the Docker image ::

$ docker build --no-cache -t emdates https://raw.githubusercontent.com/culturesofknowledge/emdates/master/dev/docker/Dockerfile
$ docker run -p 8080:8080 --name emdates emdates


Examples:

* Given a date "March 24, 1751" sourced somewhere in England, when was this on the Gregorian calendar we use today? ::

    $ curl -s localhost:8080/convert -H 'Content-type: application/json' \
           -d '{"targetCalendar": "gregorian", "year": "1751", "month": "3", "day": "24", "place": "england"}' \
           | jq -C .
    {
      "dates": [
        {
          "year": 1752,
          "month": 4,
          "day": 4,
          "notes": [
            "Based on data for place: 'England and Wales'",
            "Date after 1 January, but in this period, new year started on 03-25, so one year was added.",
            "Date on or before end of Julian calendar"
          ]
        }
      ]
    }

  Answer: *4 April 1752*.

  Because in 1751, the New Year started on March 25th (as opposed to January
  1st which we are accustomed to nowadays), the subject date of 24 March was by
  our current accounts actually in 1752.  Also, eleven days have to be added
  because the Julian calendar was in effect which, at that time, was 11 days
  behind the Gregorian calendar.

* `The execution of Charles I`__ was recorded at the time in parliament as
  happening on 30 January 1648. What date would his contemporaries in parts
  of continental Europe have recorded his execution by? ::

    curl -s localhost:8080/convert -H 'Content-type: application/json' \
         -d '{"targetCalendar": "gregorian", "year": "1648", "month": "1", "day": "30", "place": "england"}' \
         | jq -C .
    {
      "dates": [
        {
          "year": 1649,
          "month": 2,
          "day": 9,
          "notes": [
            "Based on data for place: 'England and Wales'",
            "Date after 1 January, but in this period, new year started on 03-25, so one year was added.",
            "Date on or before end of Julian calendar"
          ]
        }
      ]
    }

  Answer: *9 February 1649*.

  If we wanted the date in Julian, but with the year corrected to start on 1
  January, we ask for ``"targetCalendar": "julian"`` which yields
  the following result ::

    $ curl -s localhost:8080/convert -H 'Content-type: application/json' \
           -d '{"targetCalendar": "julian", "year": "1648", "month": "1", "day": "30", "place": "england"}' \
           | jq -C .
    {
      "dates": [
        {
          "year": 1649,
          "month": 1,
          "day": 30,
          "notes": [
            "Based on data for place: 'England and Wales'",
            "Date after 1 January, but in this period, new year started on 03-25, so one year was added.",
            "Date on or before end of Julian calendar"
          ]
        }
      ]
    }

  Answer: *30 January, 1649*

  As `this note`_ on `Charles I`_ explains, this is sometimes referred to as
  *Old Style Julian calendar*.

__ https://en.wikipedia.org/wiki/Old_Style_and_New_Style_dates#Start_of_the_year_in_the_historical_records_of_Britain_and_its_colonies_and_possessions
.. _this note: https://en.wikipedia.org/wiki/Charles_I_of_England#cite_note-1 
.. _Charles I: https://en.wikipedia.org/wiki/Charles_I_of_England
