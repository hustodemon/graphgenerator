(ns graphgenerator.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[graphgenerator started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[graphgenerator has shut down successfully]=-"))
   :middleware identity})
