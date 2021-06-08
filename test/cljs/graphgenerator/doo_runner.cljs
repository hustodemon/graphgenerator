(ns graphgenerator.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [graphgenerator.core-test]))

(doo-tests 'graphgenerator.core-test)

