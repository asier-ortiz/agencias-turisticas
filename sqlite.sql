/*TABLES*/
-----------------------------------------------------------------

CREATE TABLE EMPLOYEE
(
    EMPLOYEE_ID   INTEGER PRIMARY KEY AUTOINCREMENT,
    DNI           VARCHAR2 NOT NULL,
    NAME          VARCHAR2 NOT NULL,
    FIRST_SURNAME VARCHAR2 NOT NULL,
    BIRTH_DATE    TEXT     NOT NULL,
    NATIONALITY   VARCHAR2 NOT NULL,
    ROLE          VARCHAR2 NOT NULL,
    EMAIL         VARCHAR2 NOT NULL,
    PASSWORD      VARCHAR2 NOT NULL,
    ACTIVE        NUMBER DEFAULT 1,
    CONSTRAINT EMP_ROL_CK CHECK (ROLE IN ('ADMIN', 'EMPLEADO'))
);

CREATE TABLE CLIENT
(
    CLIENT_ID      INTEGER PRIMARY KEY AUTOINCREMENT,
    DNI            VARCHAR2 NOT NULL,
    NAME           VARCHAR2 NOT NULL,
    FIRST_SURNAME  VARCHAR2 NOT NULL,
    SECOND_SURNAME VARCHAR2,
    BIRTH_DATE     TEXT     NOT NULL,
    PROFESSION     VARCHAR2,
    ACTIVE         NUMBER DEFAULT 1
);

CREATE TABLE TOUR
(
    TOUR_ID        INTEGER PRIMARY KEY AUTOINCREMENT,
    EMPLOYEE_ID    INTEGER,
    TITLE          VARCHAR2 NOT NULL,
    DESCRIPTION    VARCHAR2,
    TOPIC          VARCHAR2 NOT NULL,
    PLACE          VARCHAR2 NOT NULL,
    STARTING_POINT VARCHAR2 NOT NULL,
    MAX_ATTENDEES  NUMBER   NOT NULL,
    PRICE          REAL     NOT NULL,
    START_DATE     TEXT     NOT NULL,
    CANCELLED      NUMBER DEFAULT 0,
    CONSTRAINT TOU_EMP_FK FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE (EMPLOYEE_ID) ON DELETE SET NULL,
    CONSTRAINT TOU_TOP_CK CHECK (TOPIC IN ('ACTIVIDAD_FAMILIAR', 'CONCIERTO', 'DANZA_TEATRO', 'DEPORTES',
                                           'EVENTOS_GASTRONOMICOS', 'EXPOSICIONES', 'FESTIVALES', 'FIESTAS_TRADICIONES',
                                           'CONGRESOS_FERIAS', 'RUTAS', 'OTROS'))
);

CREATE TABLE EMPLOYEE_REGISTRATION_CANCELLATION
(
    REGISTRATION_CANCELLATION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
    EMPLOYEE_ID                  INTEGER,
    START_DATE                   TEXT NOT NULL,
    END_DATE                     TEXT,
    FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE (EMPLOYEE_ID) ON DELETE CASCADE
);

CREATE TABLE CLIENT_REGISTRATION_CANCELLATION
(
    REGISTRATION_CANCELLATION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
    CLIENT_ID                    INTEGER,
    START_DATE                   TEXT NOT NULL,
    END_DATE                     TEXT,
    FOREIGN KEY (CLIENT_ID) REFERENCES CLIENT (CLIENT_ID) ON DELETE CASCADE
);

CREATE TABLE CLIENT_TOUR
(
    CLIENT_ID INTEGER,
    TOUR_ID   INTEGER,
    CONSTRAINT CLI_ID_FK FOREIGN KEY (CLIENT_ID) REFERENCES CLIENT (CLIENT_ID) ON DELETE CASCADE,
    CONSTRAINT TOU_CLI_ID_FK FOREIGN KEY (TOUR_ID) REFERENCES TOUR (TOUR_ID) ON DELETE CASCADE
);

CREATE TABLE BONUS
(
    BONUS_ID         INTEGER PRIMARY KEY AUTOINCREMENT,
    CLIENT_ID        INTEGER,
    BONUS_TYPE       INTEGER  NOT NULL,
    DESCRIPTION      VARCHAR2 NOT NULL,
    ATTAINTMENT_DATE TEXT     NOT NULL,
    CONSTRAINT BON_CLI_FK FOREIGN KEY (CLIENT_ID) REFERENCES CLIENT (CLIENT_ID) ON DELETE CASCADE,
    CONSTRAINT BON_TYP_CK CHECK (BONUS_TYPE IN (5, 10, 15, 20, 25))
);

CREATE TABLE EMPLOYEE_CLOCK_IN_CLOCK_OUT
(
    SESSION_ID  INTEGER PRIMARY KEY AUTOINCREMENT,
    EMPLOYEE_ID INTEGER,
    START_DATE  TEXT NOT NULL,
    END_DATE    TEXT,
    FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE (EMPLOYEE_ID) ON DELETE CASCADE
);

-----------------------------------------------------------------