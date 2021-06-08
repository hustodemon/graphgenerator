(ns graphgenerator.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [graphgenerator.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[graphgenerator started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[graphgenerator has shut down successfully]=-"))
   :middleware wrap-dev})
