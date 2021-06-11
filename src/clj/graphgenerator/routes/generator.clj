(ns graphgenerator.routes.generator
  (:require
   [graphgenerator.generator.core :as generator]
   [graphgenerator.generator.config :as config]
   [graphgenerator.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))



(defn generate-graph
  "
  Handler for generating graphs.
  Extracts important parameters from headers and query parameters.
  The source code for the graph comes in the request body.
  "
  [req]
  (let [accept-type (get-in req [:headers "accept"] "image/svg+xml")
        graph-type  (keyword (get-in req [:params :type] "graphviz"))
        program     (keyword (get-in req [:params :program] "dot"))
        fmt         (config/find-format-by-parameter :media-type accept-type)]
    ;; todo error handling and reporting
    (try
      {:status  200
       :headers {"Content-Type" accept-type}
       :body
       (generator/generate-graph
        {:type    graph-type
         :program program
         :fmt     fmt
         :src     (:body req)})}
      (catch clojure.lang.ExceptionInfo e
        (response/internal-server-error (-> e .getData :msg))))))


(defn generator-routes []
  ["/generate"
   {:middleware [middleware/wrap-formats]
    :post generate-graph}])
