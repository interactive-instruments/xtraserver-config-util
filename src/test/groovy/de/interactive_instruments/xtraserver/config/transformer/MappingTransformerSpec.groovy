package de.interactive_instruments.xtraserver.config.transformer

import de.interactive_instruments.xtraserver.config.api.Doc
import spock.lang.Specification
import spock.lang.Unroll

class MappingTransformerSpec extends Specification {

    def 'print docs'() {

        given:

        TransformerSpecs.get().each {spec -> println Doc.from(spec)}

        //def mapping = MultiJoinSpec.sameColumnDifferentPaths().given()

        //expect:

        //new MappingTransformerMultiJoins(mapping).transform() == MultiJoinSpec.sameColumnDifferentPaths().expected()

    }

    //TODO: CHAINED
    @Unroll
    def '#title [ISOLATED]'() {

        expect:

        transform(given) == expected

        where:

        useCase << TransformerSpecs.get().collect {spec -> spec.useCases()}.flatten()
        transform << TransformerSpecs.get().collect {spec -> [spec.&transform].multiply(spec.useCases().size())}.flatten()

        title = useCase.title()
        description = useCase.description()
        given = useCase.given()
        expected = useCase.expected()
    }

}
