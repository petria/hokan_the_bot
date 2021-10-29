package org.freakz.hokan_ng_springboot.bot.engine.command.handlers;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import org.cheffo.jeplite.JEP;
import org.cheffo.jeplite.ParseException;
import org.freakz.hokan_ng_springboot.bot.common.events.EngineResponse;
import org.freakz.hokan_ng_springboot.bot.common.events.InternalRequest;
import org.freakz.hokan_ng_springboot.bot.common.exception.HokanException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings.ARG_EXPRESSION;

/**
 * User: petria
 * Date: 11/27/13
 * Time: 3:20 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
@Component
@Scope("prototype")
public class CalcCmd extends Cmd {

    private final JEP jep;
    private static int resultCounter = 0;
    private static Map<String, String> resultMap = new HashMap<>();

    public CalcCmd() {
        super();
        setHelp("I am your pocket calculator.");

        jep = new JEP();
        jep.addStandardConstants();
        jep.addStandardFunctions();

        UnflaggedOption flg = new UnflaggedOption(ARG_EXPRESSION)
                .setRequired(true)
                .setGreedy(false);
        registerParameter(flg);

    }


    @Override
    public void handleRequest(InternalRequest request, EngineResponse response, JSAPResult results) throws HokanException {
        String result;
        String expression = results.getString(ARG_EXPRESSION);
        for (String key : resultMap.keySet()) {
            expression = expression.replaceAll(key, resultMap.get(key));
        }
        jep.parseExpression(expression);

        String error = jep.getErrorInfo();

        if (error != null) {
            response.addResponse("Expression parse error: " + error.replaceAll("\"", ""));
        } else {
            try {
                resultCounter++;
                String resultKey = "RES" + resultCounter;
                result = expression + " = " + jep.getValue();
                resultMap.put(resultKey, jep.getValue() + "");
                response.addResponse(String.format("%s: %s", resultKey, result));
            } catch (ParseException e) {
                throw new HokanException(e);
            }
        }
    }
}
