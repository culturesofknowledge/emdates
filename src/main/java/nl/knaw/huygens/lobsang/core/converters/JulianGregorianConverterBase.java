package nl.knaw.huygens.lobsang.core.converters;

import nl.knaw.huygens.lobsang.api.YearMonthDay;

/*
 * See also:
 * https://stackoverflow.com/questions/47001216/how-to-convert-a-gregorian-date-to-julian-date-with-the-java-8-date
 * -time-api
 * http://time4j.net/javadoc-en/net/time4j/calendar/HistoricCalendar.html
 * http://www.time4j.net/tutorial/introduction.html
 * https://stevemorse.org/jcal/julian.html
 * https://en.wikipedia.org/wiki/Julian_day
 */
abstract class JulianGregorianConverterBase implements CalendarConverter {

  YearMonthDay findJulianGregorianDate(int julGregF) {
    // 2. e = r × f + v
    final int e = 4 * julGregF + 3;

    // 3. g = mod(e, p) div r
    final int g = (e % 1461) / 4;

    // 4. h = u × g + w
    final int h = 5 * g + 2;

    // 5. D = (mod(h, s)) div u + 1
    final int D = ((h % 153) / 5) + 1;

    // 6. M = mod(h div s + m, n) + 1
    final int M = (((h / 153) + 2) % 12) + 1;

    // 7. Y = (e div p) - y + (n + m - M) div n
    final int Y = (e / 1461) - 4716 + ((12 + 2 - M) / 12);

    // D, M, and Y are the numbers of the day, month, and year respectively for the afternoon at the beginning of the
    // given Julian day.

    return new YearMonthDay(Y, M, D);
  }
}
