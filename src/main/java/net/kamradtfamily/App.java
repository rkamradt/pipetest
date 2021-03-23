/*
 * The MIT License
 *
 * Copyright 2021 randalkamradt.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.kamradtfamily;

import io.cucumber.gherkin.Gherkin;
import io.cucumber.messages.IdGenerator;
import io.cucumber.messages.Messages;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;

import static java.util.Collections.singletonList;

public class App
{
    private static IdGenerator idGenerator = new IdGenerator.Incrementing();
    public static void main( String[] args )
    {
        Mono<Map<String, Object>> result = Gherkin
                .fromPaths(singletonList("testdata/good/scenario.feature"),
                    false,
                    false,
                    true,
                    idGenerator)
                .flatMap(e -> e.getPickle()
                        .getStepsList()
                        .stream()
                        .sequential())
                .map(p -> transformToFunction(p))
                .reduce(Mono.just(new HashMap<>()),
                        (r,f) -> r.map(f),
                        (r1,r2) -> r1.then(r2) );
        result.subscribe(c -> System.out.println(c),
                e -> System.out.println(e));
    }

    public static Function<Map<String, Object>, Map<String, Object>>
                transformToFunction(Messages.Pickle.PickleStep step) {
        try {
            Class<Function<Map<String, Object>, Map<String, Object>>> clss =
                    (Class<Function<Map<String, Object>, Map<String, Object>>>)
                            Class.forName("net.kamradtfamily.Steps$"
                                    + step.getText().replace(' ', '_'));
            return clss.newInstance();
        } catch(Exception ex) {
            throw new RuntimeException("unable to create function for step "
                    + step.getText());
        }
    }

}
