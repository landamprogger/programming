package app.query.queryBuilder;

import app.Interpretator;
import app.Validator;
import app.query.CommandType;
import controller.components.serviceMediator.Service;

/**
 * Factory for creating concrete QueryBuilder depending on command type.
 */
public class QueryBuilderFactory implements Service {
    private Validator validator;
    private Interpretator interpretator;

    public QueryBuilderFactory(Validator validator, Interpretator interpretator) {
        this.validator = validator;
        this.interpretator = interpretator;
    }

    public QueryBuilder getQueryBuilder (CommandType type){
        switch (type){
            case SIMPLE_COMMAND:
                return new QueryBuilderForSimpleCommands(validator, interpretator);
            case COMMAND_WITHOUT_ARGUMENTS:
                return new QueryBuilderForCommandsWithoutArguments(validator, interpretator);
            case COMPOUND_COMMAND:
                return new QueryBuilderForCompoundCommands(validator, interpretator);
            default:
                throw new IllegalArgumentException("QueryBuilder can not be created. Current CommandType is " + type);

        }
    }
}
