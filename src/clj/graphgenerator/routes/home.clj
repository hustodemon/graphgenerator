(ns graphgenerator.routes.home
  (:require
   [graphgenerator.layout :as layout]
   [clojure.java.io :as io]
   [graphgenerator.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [rhizome.viz :as viz]))


(defn home-page [request]
  (layout/render request "home.html"))


(defn generate-graph [graph]
  (def xxx graph)
  ;; (let [graph {:a [:b :c]
  ;;              :b [:c]
  ;;              :c [:a]}]
  (viz/graph->svg (keys graph) graph
                  :node->descriptor (fn [n] {:label n})))
;;)

(def digraph "digraph test123 {
    a -> b -> c;
    a -> {x y};
    b [shape=box];
    c [label=\"hello\nworld\",color=blue,fontsize=24,
      fontname=\"Palatino-Italic\",fontcolor=red,style=filled];
    a -> z [label=\"hi\", weight=100];
    x -> z [label=\"multi-line\nlabel\"];
    edge [style=dashed,color=red];
    b -> x;
    {rank=same; b x}
}
")

(defn generate-graph-dot [dot-str]
  (let [{:keys [exit out err]} (clojure.java.shell/sh "/usr/bin/dot" "-Tsvg" :in dot-str)]
    (if (= 0 exit)
      out
      (throw (ex-info "Dot error" {:msg err})))))


(comment
  (try
    (generate-graph-dot "aoeu")
    (catch Throwable e
      e)
    )
  (clojure.java.shell/sh "/usr/bin/dot" "-Tsvg" :in "wahnsinn")
)


(def g
  {:a [:b :c]
   :b [:c]
   :c [:a]}
  )


(defn home-routes []
  [""
   {:middleware [;; middleware/wrap-csrf. todo revive
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/generate-dot" {:post (fn [req]
                             (def x req)
                             (try
                               (response/ok
                                (generate-graph-dot
                                 (:body-params x)))
                               (catch Throwable e
                                 (response/internal-server-error (-> e .getData :msg)))))}]
   ["/generate" {:post (fn [req]
                         (def x req)
                         (try
                           (response/ok
                            (generate-graph
                             (clojure.edn/read-string
                              (:body-params x))
                             ));; todo catch only exception-info
                           (catch Throwable e
                             ;; todo ISE - something else
                             (response/internal-server-error (str e)))))}]
   ["/docs" {:get (fn [_]
                    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]])

