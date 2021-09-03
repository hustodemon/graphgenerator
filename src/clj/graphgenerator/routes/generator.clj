(ns graphgenerator.routes.generator
  (:require
   [graphgenerator.generator.core :as generator]
   [graphgenerator.generator.config :as config]
   [graphgenerator.middleware :as middleware]
   [clojure.tools.logging :as log]
   [ring.util.response]
   [ring.util.http-response :as response]))


(defn generate-graph
  "
  Handler for generating graphs.
  Extracts important parameters from headers and query parameters.
  The source code for the graph comes in the request body.
  "
  [req]
  (let [accept-type    (get-in req [:headers "accept"] "image/svg+xml")
        content-length (get-in req [:headers "content-length"])
        graph-type     (keyword (get-in req [:params :type] "graphviz"))
        program        (keyword (get-in req [:params :program] "dot"))
        fmt            (config/find-format-by-parameter :media-type accept-type)]
    ;; todo error handling and reporting
    (cond
      ;; maybe these validations could be written as a middleware?
      (not (pos-int? (Integer/parseInt content-length)))
      {:status 411
       :body   "You must specify valid content length"}

      (> (Integer/parseInt content-length) config/max-source-length-bytes)
      {:status 418
       :body   (str "Content too long, max length (bytes): " config/max-source-length-bytes)}

      :else
      (try
        (let [params {:type    graph-type
                      :program program
                      :fmt     fmt
                      :src     (:body req)}]
          (log/info (str "Generating graph from params: " params))
          {:status  200
           :headers {"Content-Type" accept-type}
           :body    (generator/generate-graph params)})
        (catch clojure.lang.ExceptionInfo e
          (response/internal-server-error (-> e .getData :msg)))))))


(defn generator-routes []
  ["/generate"
   {:middleware [middleware/wrap-formats]
    :post generate-graph}])
