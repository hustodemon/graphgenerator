(ns graphgenerator.routes.home
  (:require
   [graphgenerator.generator.core :as generator]
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


(defn generate-graph [req]
  (try
    (response/ok
     (generator/generate-graph
      {:type (keyword (get-in req [:params :type]))
       :tool (get-in req [:params :tool])
       :src  (:body req)}))
    (catch Throwable e
      (response/internal-server-error (-> e .getData :msg)))))


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
