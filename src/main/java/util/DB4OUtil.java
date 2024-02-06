package util;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.events.*;
import com.db4o.foundation.Iterator4;
import com.db4o.internal.FrozenObjectInfo;
import com.db4o.internal.LazyObjectReference;
import com.db4o.query.Predicate;
import model.*;

public class DB4OUtil {
    public final static String dbFileName = "agenciasturisticas.yap";
    private static ObjectContainer dataBaseContainer;
    private static EventRegistry eventRegistry;

    public static synchronized ObjectContainer getDataBaseContainer() {
        if (dataBaseContainer == null || eventRegistry == null) {
            EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
            config.common().objectClass(Employee.class).cascadeOnDelete(true);
            config.common().objectClass(Employee.class).cascadeOnUpdate(true);
            config.common().objectClass(EmployeeRegistrationCancellation.class).cascadeOnUpdate(true);
            config.common().objectClass(EmployeeClockInClockOut.class).cascadeOnUpdate(true);
            config.common().objectClass(Client.class).cascadeOnDelete(true);
            config.common().objectClass(Client.class).cascadeOnUpdate(true);
            config.common().objectClass(ClientRegistrationCancelation.class).cascadeOnUpdate(true);
            config.common().objectClass(Bonus.class).cascadeOnUpdate(true);
            config.common().objectClass(Tour.class).cascadeOnUpdate(true);
            dataBaseContainer = Db4oEmbedded.openFile(config, dbFileName);
            eventRegistry = addEvents(dataBaseContainer);
        }
        return dataBaseContainer;
    }

    private static EventRegistry addEvents(ObjectContainer objectContainer) {
        EventRegistry eventRegistry = EventRegistryFactory.forObjectContainer(objectContainer);

        eventRegistry.committed().addListener((event4, commitEventArgs) -> {
            for (Iterator4 it = commitEventArgs.added().iterator(); it.moveNext(); ) {
                LazyObjectReference reference = (LazyObjectReference) it.current();
                //System.out.println("Added" + reference.getObject());
            }
            for (Iterator4 it = commitEventArgs.updated().iterator(); it.moveNext(); ) {
                LazyObjectReference reference = (LazyObjectReference) it.current();
                //System.out.println("Updated" + reference.getObject());
            }
            for (Iterator4 it = commitEventArgs.deleted().iterator(); it.moveNext(); ) {
                FrozenObjectInfo deletedInfo = (FrozenObjectInfo) it.current();
                //System.out.println("Deleted" + deletedInfo.getObject()); // Guarda una referencia del objeto después de borrarlo por si necesitas hacer algo con ella
            }
        });

        eventRegistry.creating().addListener((event4, args) -> {
            if (args.object() instanceof Employee) {
                Employee employee = (Employee) args.object();
                if (employeeAlreadyExists(employee)) {
                    args.cancel();
                    System.err.println("Error: ID already exists...");
                }
            } else if (args.object() instanceof Client) {
                Client client = (Client) args.object();
                if (clientAlreadyExists(client)) {
                    args.cancel();
                    System.err.println("Error: ID already exists...");
                }
            } else if (args.object() instanceof Tour) {
                Tour tour = (Tour) args.object();
                if (tourAlreadyExists(tour)) {
                    args.cancel();
                    System.err.println("Error: ID already exists...");
                }
            } else if (args.object() instanceof EmployeeRegistrationCancellation) {
                EmployeeRegistrationCancellation employeeRegistrationCancellation = (EmployeeRegistrationCancellation) args.object();
                if (employeeRegistrationCancellationAlreadyExists(employeeRegistrationCancellation)) {
                    args.cancel();
                    System.err.println("Error: ID already exists...");
                }
            } else if (args.object() instanceof ClientRegistrationCancelation) {
                ClientRegistrationCancelation clientRegistrationCancelation = (ClientRegistrationCancelation) args.object();
                if (clientRegistrationCancellationAlreadyExists(clientRegistrationCancelation)) {
                    args.cancel();
                    System.err.println("Error: ID already exists...");
                }
            } else if (args.object() instanceof Bonus) {
                Bonus bonus = (Bonus) args.object();
                if (bonusAlreadyExists(bonus)) {
                    args.cancel();
                    System.err.println("Error: ID already exists...");
                }
            } else if (args.object() instanceof EmployeeClockInClockOut) {
                EmployeeClockInClockOut employeeClockInClockOut = (EmployeeClockInClockOut) args.object();
                if (employeeClockInClockOutAlreadyExists(employeeClockInClockOut)) {
                    args.cancel();
                    System.err.println("Error: ID already exists...");
                }
            }
        });
        eventRegistry.deleting().addListener((event4, args) -> {
            if (args.object() instanceof Employee) {
                Employee employee = (Employee) args.object();
                if (!employeeAlreadyExists(employee)) {
                    args.cancel();
                    System.err.println("Error: ID doesn't exists...");
                }
            } else if (args.object() instanceof Client) {
                Client client = (Client) args.object();
                if (!clientAlreadyExists(client)) {
                    args.cancel();
                    System.err.println("Error: ID doesn't exists...");
                }
            } else if (args.object() instanceof Tour) {
                Tour tour = (Tour) args.object();
                if (tourAlreadyExists(tour)) {
                    args.cancel();
                    System.err.println("Error: ID doesn't exists...");
                }
            } else if (args.object() instanceof EmployeeRegistrationCancellation) {
                EmployeeRegistrationCancellation employeeRegistrationCancellation = (EmployeeRegistrationCancellation) args.object();
                if (!employeeRegistrationCancellationAlreadyExists(employeeRegistrationCancellation)) {
                    args.cancel();
                    System.err.println("Error: ID doesn't exists...");
                }
            } else if (args.object() instanceof ClientRegistrationCancelation) {
                ClientRegistrationCancelation clientRegistrationCancelation = (ClientRegistrationCancelation) args.object();
                if (!clientRegistrationCancellationAlreadyExists(clientRegistrationCancelation)) {
                    args.cancel();
                    System.err.println("Error: ID doesn't exists...");
                }
            } else if (args.object() instanceof Bonus) {
                Bonus bonus = (Bonus) args.object();
                if (!bonusAlreadyExists(bonus)) {
                    args.cancel();
                    System.err.println("Error: ID doesn't exists...");
                }
            } else if (args.object() instanceof EmployeeClockInClockOut) {
                EmployeeClockInClockOut employeeClockInClockOut = (EmployeeClockInClockOut) args.object();
                if (!employeeClockInClockOutAlreadyExists(employeeClockInClockOut)) {
                    args.cancel();
                    System.err.println("Error: ID doesn't exists...");
                }
            }
        });
        return eventRegistry;
    }

    private static Boolean employeeAlreadyExists(Employee employee) {
        ObjectSet<Employee> employees = dataBaseContainer.query(new Predicate<>() {
            @Override
            public boolean match(Employee e) {
                return e.equals(employee);
            }
        });
        return !employees.isEmpty();
    }

    private static Boolean clientAlreadyExists(Client client) {
        ObjectSet<Client> clients = dataBaseContainer.query(new Predicate<>() {
            @Override
            public boolean match(Client c) {
                return c.equals(client);
            }
        });
        return !clients.isEmpty();
    }

    private static Boolean tourAlreadyExists(Tour tour) {
        ObjectSet<Tour> tours = dataBaseContainer.query(new Predicate<>() {
            @Override
            public boolean match(Tour t) {
                return t.equals(tour);
            }
        });
        return !tours.isEmpty();
    }

    private static Boolean employeeRegistrationCancellationAlreadyExists(EmployeeRegistrationCancellation employeeRegistrationCancellation) {
        ObjectSet<EmployeeRegistrationCancellation> employeeRegistrationCancellations = dataBaseContainer.query(new Predicate<>() {
            @Override
            public boolean match(EmployeeRegistrationCancellation ecr) {
                return ecr.equals(employeeRegistrationCancellation);
            }
        });
        return !employeeRegistrationCancellations.isEmpty();
    }

    private static Boolean clientRegistrationCancellationAlreadyExists(ClientRegistrationCancelation clientRegistrationCancelation) {
        ObjectSet<ClientRegistrationCancelation> clientRegistrationCancelations = dataBaseContainer.query(new Predicate<>() {
            @Override
            public boolean match(ClientRegistrationCancelation ccr) {
                return ccr.equals(clientRegistrationCancelation);
            }
        });
        return !clientRegistrationCancelations.isEmpty();
    }

    private static Boolean bonusAlreadyExists(Bonus bonus) {
        ObjectSet<Bonus> bonuses = dataBaseContainer.query(new Predicate<>() {
            @Override
            public boolean match(Bonus b) {
                return b.equals(bonus);
            }
        });
        return !bonuses.isEmpty();
    }

    private static Boolean employeeClockInClockOutAlreadyExists(EmployeeClockInClockOut employeeClockInClockOut) {
        ObjectSet<EmployeeClockInClockOut> employeeClockInClockOuts = dataBaseContainer.query(new Predicate<>() {
            @Override
            public boolean match(EmployeeClockInClockOut e) {
                return e.equals(employeeClockInClockOut);
            }
        });
        return !employeeClockInClockOuts.isEmpty();
    }
}