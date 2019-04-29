/*
 * Heaviliy based on:
 * https://github.com/ksclarke/freelib-edtf/blob/master/src/main/antlr/info/freelibrary/edtf/internal/ExtendedDateTimeFormat.g4
 */

grammar Iso8601Format;

/** Parser rule wrapper **/
iso8601: level0 | level1;

/** Level 0: Tokens **/
Dash : '-';

Year : PositiveYear | NegativeYear | YearZero;
NegativeYear : Dash PositiveYear;
PositiveYear
    : PositiveDigit Digit Digit Digit
    | Digit PositiveDigit Digit Digit
    | Digit Digit PositiveDigit Digit
    | Digit Digit Digit PositiveDigit
    ;
Digit : PositiveDigit | '0';
PositiveDigit : '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9';
YearZero : '0000';
Month : OneThru12;
MonthDay
    : ( '01' | '03' | '05' | '07' | '08' | '10' | '12' ) Dash OneThru31
    | ( '04' | '06' | '09' | '11' ) Dash OneThru30
    | '02' Dash OneThru29
    ;
MonthDayCompact
    : ( '01' | '03' | '05' | '07' | '08' | '10' | '12' ) OneThru31
    | ( '04' | '06' | '09' | '11' ) OneThru30
    | '02' OneThru29
    ;
YearMonth : Year Dash Month;
YearMonthDay : Year Dash MonthDay;
YearMonthDayCompact : Year MonthDayCompact;
OneThru12
    : '01' | '02' | '03' | '04' | '05' | '06'  | '07' | '08' | '09' | '10'
    | '11' | '12'
    ;
OneThru13 : OneThru12 | '13';
OneThru23
    : OneThru13 | '14' | '15' | '16' | '17'  | '18' | '19' | '20' | '21'
    | '22' | '23'
    ;
ZeroThru23 : '00' | OneThru23;
OneThru29 : OneThru23 | '24' | '25' | '26' | '27' | '28' | '29';
OneThru30 : OneThru29 | '30';
OneThru31 : OneThru30 | '31';
OneThru59 : OneThru31
    | '32' | '33' | '34' | '35' | '36' | '37' | '38' | '39' | '40' | '41'
    | '42' | '43' | '44' | '45' | '46' | '47' | '48' | '49' | '50' | '51'
    | '52' | '53' | '54' | '55' | '56' | '57' | '58' | '59'
    ;

/** Level 0 Parser rules **/
level0 : year | yearMonth | yearMonthDay | yearMonthDayCompact;
year : Year;
yearMonth : YearMonth;
yearMonthDay : YearMonthDay;
yearMonthDayCompact : YearMonthDayCompact;

/** Level 1: Tokens **/
Questionmark : '?';
Tilde : '~';
PercentSign : '%';
X : 'X';
YearUnspecifiedMonth: Year Dash X X;
UnspecifiedYearAndMonth: X X X X Dash X X;
YearMonthUnspecifiedDay : YearMonth Dash X X;
YearUnspecifiedMonthAndDay : Year Dash X X Dash X X;
UnspecifiedYearAndMonthAndDay : X X X X Dash X X Dash X X;
UnspecifiedSingleYear : PositiveUnspecifiedSingleYear | NegativeUnspecifiedSingleYear;
NegativeUnspecifiedSingleYear : Dash PositiveUnspecifiedSingleYear;
PositiveUnspecifiedSingleYear
    : PositiveDigit Digit Digit X
    | Digit PositiveDigit Digit X
    | Digit Digit PositiveDigit X
    | Digit Digit Digit X
    ;
UnspecifiedDecadeAndSingleYear : PositiveUnspecifiedDecadeAndSingleYear | NegativeUnspecifiedDecadeAndSingleYear;
NegativeUnspecifiedDecadeAndSingleYear : Dash PositiveUnspecifiedDecadeAndSingleYear;
PositiveUnspecifiedDecadeAndSingleYear
    : PositiveDigit Digit X X
    | Digit PositiveDigit X X
    | Digit Digit X X
    ;
UnspecifiedCenturyAndDecadeAndSingleYear
    : PositiveUnspecifiedCenturyAndDecadeAndSingleYear
    | NegativeUnspecifiedCenturyAndDecadeAndSingleYear
    ;
PositiveUnspecifiedCenturyAndDecadeAndSingleYear
    : PositiveDigit X X X
    | Digit X X X
    ;
NegativeUnspecifiedCenturyAndDecadeAndSingleYear : Dash PositiveUnspecifiedCenturyAndDecadeAndSingleYear;
UnspecifiedPositiveYear : X X X X;
UnspecifiedNegativeYear : Dash X X X X;


/** Level 1 ParserRules **/
level1
    : yearUncertain
    | yearApproximate
    | yearUncertainApproximate
    | yearMonthUncertain
    | yearMonthApproximate
    | yearMonthUncertainApproximate
    | yearMonthDayUncertain
    | yearMonthDayApproximate
    | yearMonthDayUncertainApproximate
    | yearMonthUnspecifiedDay
    | yearUnspecifiedMonthAndDay
    | unspecifiedYearAndMonthAndDay
    | yearUnspecifiedMonth
    | unspecifiedYearAndMonth
    | unspecifiedSingleYear
    | unspecifiedDecadeAndSingleYear
    | unspecifiedCenturyAndDecadeAndSingleYear
    | unspecifiedPositiveYear
    | unspecifiedNegativeYear
    ;
yearUncertain: Year Questionmark;
yearApproximate: Year Tilde;
yearUncertainApproximate: Year PercentSign;
yearMonthUncertain : YearMonth Questionmark;
yearMonthApproximate : YearMonth Tilde;
yearMonthUncertainApproximate : YearMonth PercentSign;
yearMonthDayUncertain : YearMonthDay Questionmark;
yearMonthDayApproximate : YearMonthDay Tilde;
yearMonthDayUncertainApproximate : YearMonthDay PercentSign;
yearMonthUnspecifiedDay : YearMonthUnspecifiedDay;
yearUnspecifiedMonthAndDay : YearUnspecifiedMonthAndDay;
unspecifiedYearAndMonthAndDay : UnspecifiedYearAndMonthAndDay;
yearUnspecifiedMonth : YearUnspecifiedMonth;
unspecifiedYearAndMonth : UnspecifiedYearAndMonth;
unspecifiedSingleYear : UnspecifiedSingleYear;
unspecifiedDecadeAndSingleYear : UnspecifiedDecadeAndSingleYear;
unspecifiedCenturyAndDecadeAndSingleYear : UnspecifiedCenturyAndDecadeAndSingleYear;
unspecifiedPositiveYear: UnspecifiedPositiveYear;
unspecifiedNegativeYear: UnspecifiedNegativeYear;




