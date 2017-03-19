(ns todomvc.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]
            [clara.rules :refer [query]]
            [todomvc.util :refer [entities-where]]
            [todomvc.rules :refer [find-done-count]]
            [todomvc.events :refer [get-todos]]))

(defn get-visibility-filter [db]
  (println "Got visibility filter" (entities-where db :ui/visibility-filter))
  (:ui/visibility-filter
    (first (entities-where db :ui/visibility-filter))))

(reg-sub :showing get-visibility-filter)

(reg-sub :todos
  (fn [session]
    (let [todos (entities-where session :todo/title)]
      (println "Got todos: " todos)
      todos)))


(reg-sub :visible-todos
  (fn [session] (entities-where session :todo/visible)))

(reg-sub
  :all-complete?
  :<- [:todos]
  (fn [todos _]
    (seq todos)))

(defn get-done-count [session]
  (let [done-count (:?count (first (query session find-done-count)))]
    (println "Got done count" done-count)
    (or done-count 0)))
(reg-sub :completed-count get-done-count)

(reg-sub
  :footer-counts
  :<- [:todos]
  :<- [:completed-count]
  (fn [[todos completed] _]
    [(- (count todos) completed) completed]))
