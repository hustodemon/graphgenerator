(ns graphgenerator.db
  "
  Contains the initial contents of the database.
  "
  (:require [graphgenerator.generator.config :as config]))


(def programms [
"graph {
  rankdir=LR

  a[label=\"α\"]
  b[label=\"β\"]
  c[label=\"γ\"]
  d[label=\"δ\"]
  e[label=\"ε\"]
  f[label=\"ζ\"]

  a -- b
  b -- c
  c -- d;
  d -- e;
  e -- f;
  f -- a;
}
"

"
digraph {
    rankdir=LR
 
    a[label=\"α\"]
    b[label=\"β\"]
    c[label=\"γ\"]
    d[label=\"δ\"]
    e[label=\"ε\"]
    f[label=\"ζ\"]
 
    subgraph cluster1 {
        rank = same;
        style = \"filled\";
        fillcolor = \"#80ff80\";
        a;
        a1;
        a2;
    }
 
    subgraph cluster2 {
        rank = same;
        style = \"filled\";
        fillcolor = \"#80ffff\";
        b;
        b1;
        b2;
    }
 
    subgraph cluster3 {
        rank = same;
        style = \"filled\";
        fillcolor = \"#8080ff\";
        c;
        c1;
        c2;
    }
 
    subgraph cluster4 {
        rank = same;
        style = \"filled\";
        fillcolor = \"#ff80ff\";
        d;
        d1;
        d2;
    }
 
    subgraph cluster5 {
        rank = same;
        style = \"filled\";
        fillcolor = \"#ff8080\";
        e;
        e1;
        e2;
    }
 
    subgraph cluster6 {
        rank = same;
        style = \"filled\";
        fillcolor = \"#ffff80\";
        f;
        f1;
        f2;
    }
 
    a -> b;
    b -> c;
    c -> d;
    d -> e;
    e -> f;
    f -> a;
 
    b -> a;
    c -> b;
    d -> c;
    e -> d;
    f -> e;
    a -> f;
 
    a -> a1;
    a -> a2;
    b -> b1;
    b -> b2;
    c -> c1;
    c -> c2;
    d -> d1;
    d -> d2;
    e -> e1;
    e -> e2;
    f -> f1;
    f -> f2;
}
"              

"{
  :a [:b :c]
  :b [:c]
  :c [:a]
}
"

"
{
  \"Algol\" [\"K&R C\", \"Pascal\", \"Modula\"],
  \"K&R C\" [\"ANSI C\", \"C with classes\"],
  \"C with classes\" [\"C++\"],
  \"ANSI C\" [\"C89\"],
  \"C89\" [\"C99\"],
  \"Pascal\" nil,
  \"Modula\" nil,
  \"C++\" nil,
  \"C99\" nil
}
"])


;; todo do not steal from better ppl and come up with custom examples :)
(def presets
  {:dot     [{:id 1 :label "preset1dot" :text (nth programms 0)}
             {:id 2 :label "preset2dot" :text (nth programms 1)}]
   :rhizome [{:id 1 :label "Simple directed graph" :text (nth programms 2)}
             {:id 2 :label "Programming languages" :text (nth programms 3)}]})


(def graphviz-types
  (map
   (fn [[id {:keys [label]}]] {:id id :label label})
   config/graphviz-programs))


(def first-graphviz-type
  (-> graphviz-types first :id))


(def initial-db
  {:generator/graph-types         [{:id    :dot
                                    :label "Dot"}
                                   {:id    :rhizome
                                    :label "Rhizome"}]
   :generator/selected-graph-type :dot

   ;; configured in shared config
   :generator/graphviz-types         graphviz-types
   :generator/selected-graphviz-type first-graphviz-type

   :generator/presets         presets
   :generator/selected-preset [first-graphviz-type 0]

   :generator/input        ""
   :generator/in-progress? false})



