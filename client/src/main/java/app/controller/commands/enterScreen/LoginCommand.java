package app.controller.commands.loginScreen;

import adapter.LoggerAdapter;
import client.controller.services.connectionService.ConnectionService;
import controller.command.Command;
import controller.command.exception.CommandExecutionException;
import org.apache.commons.configuration2.Configuration;
import query.Query;
import response.Response;
import response.Status;

import java.util.HashMap;
import java.util.Map;

public final class LoginCommand extends Command {
    private static final LoggerAdapter LOGGER_ADAPTER = LoggerAdapter.createDefault(LoginCommand.class.getSimpleName());


    private ConnectionService connectionService;

    /**
     * Use for creating command via factories.
     */
    public LoginCommand(String commandName, Map<String, String> arguments, Configuration configuration) {
        super(commandName, arguments, configuration);
    }

    @Override
    protected Response processExecution() throws CommandExecutionException {
        String userInput = arguments.get("userInput");
        String[] subStrings = userInput.split(" +");

        if (subStrings.length != 3) {
            return new Response(Status.BAD_REQUEST, "Login command format: \"login LOGIN PASSWORD\"");
        }

        String login = subStrings[1];
        String password = subStrings[2];

        Map<String, String> queryArguments = new HashMap<>();
        queryArguments.put("login", login);
        queryArguments.put("password", password);

        Query query = new Query("login", queryArguments, "");
        return connectionService.send(query);
    }
}
