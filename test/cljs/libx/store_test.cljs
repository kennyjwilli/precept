(ns libx.store-test
  (:require [cljs.test :refer [run-tests]]
            [libx.core :as core]
            [libx.util :as util]
            [libx.tuplerules :refer [def-tuple-session]]
            [libx.state :as state])
  (:require-macros [cljs.test :refer [deftest async use-fixtures testing is]]))

(enable-console-print!)
;(def-tuple-session my-session)

(defn clear-subs []
  (swap! state/state update :subscriptions (fn [_] {})))

(defn clear-session []
  (swap! state/state update :session (fn [_] {})))

(defn reset-store [] (reset! state/store))

(defn before-each []
  (clear-subs)
  (clear-session)
  (reset-store))


(def subdef [:foo])

(def the-update {:foo 1 :bar 2 :baz 3})

(defn do-update-for-sub [subdef]
  (swap! state/store update subdef (fn [_] the-update)))

(defn lens-for-sub [subdef] (get (:subscriptions @state/state) subdef))


(deftest fail
  (is (= [1 2 3 4] [1 2 3 4])))

