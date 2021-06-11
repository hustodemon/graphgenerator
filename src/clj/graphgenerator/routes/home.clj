(ns graphgenerator.routes.home
  (:require
   [graphgenerator.generator.core :as generator]
   [graphgenerator.generator.config :as config]
   [graphgenerator.layout :as layout]
   [clojure.java.io :as io]
   [graphgenerator.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))


(defn home-page [request]
  (layout/render request "home.html"))


(defn docs [_]
  (fn [_]
    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
        (response/header "Content-Type" "text/plain; charset=utf-8"))))


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


(defn home-routes []
  [""
   {:middleware [middleware/wrap-formats]} ;; todo csrf when needed
   ["/" {:get home-page}]
   ["/generate-graph"
    {:post generate-graph}]
   ["/generate"
    {:post generate-graph}]
   ["/docs"
    {:get docs}]])
