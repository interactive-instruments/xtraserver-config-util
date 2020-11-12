package de.interactive_instruments.xtraserver.config.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface Spec {

  String title();

  String description();

  XtraServerMapping transform(XtraServerMapping xtraServerMapping);

  List<UseCase> useCases();

  static Builder builder() {
    return new Builder();
  }

  class Builder {
    private String title;
    private String description;
    private Function<XtraServerMapping, XtraServerMapping> transform;
    private final List<UseCase> useCases;

    public Builder() {
      this.title = "";
      this.description = "";
      this.transform = Function.identity();
      this.useCases = new ArrayList<>();
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder transform(Function<XtraServerMapping, XtraServerMapping> transform) {
      this.transform = transform;
      return this;
    }

    public Builder useCase(UseCase useCase) {
      this.useCases.add(useCase);
      return this;
    }

    public Spec build() {

      return new Spec() {
        @Override
        public String title() {
          return title;
        }

        @Override
        public String description() {
          return description;
        }

        @Override
        public XtraServerMapping transform(XtraServerMapping xtraServerMapping) {
          return transform.apply(xtraServerMapping);
        }

        @Override
        public List<UseCase> useCases() {
          return useCases;
        }
      };
    }
  }
}
