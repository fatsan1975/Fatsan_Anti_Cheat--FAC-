package io.fatsan.fac.check;

import io.fatsan.fac.model.CheckCategory;
import io.fatsan.fac.model.CheckResult;
import io.fatsan.fac.model.NormalizedEvent;

public interface Check {
  String name();

  CheckCategory category();

  CheckResult evaluate(NormalizedEvent event);
}
