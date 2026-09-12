(function (factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'react', 'react/jsx-runtime', './kotlin-kotlin-stdlib.js', './kotlin-js.js', './kotlinx-coroutines-core.js'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('react'), require('react/jsx-runtime'), require('./kotlin-kotlin-stdlib.js'), require('./kotlin-js.js'), require('./kotlinx-coroutines-core.js'));
  else {
    if (typeof react === 'undefined') {
      throw new Error("Error loading module 'kotlin-react'. Its dependency 'react' was not found. Please, check whether 'react' is loaded prior to 'kotlin-react'.");
    }
    if (typeof globalThis['react/jsx-runtime'] === 'undefined') {
      throw new Error("Error loading module 'kotlin-react'. Its dependency 'react/jsx-runtime' was not found. Please, check whether 'react/jsx-runtime' is loaded prior to 'kotlin-react'.");
    }
    if (typeof globalThis['kotlin-kotlin-stdlib'] === 'undefined') {
      throw new Error("Error loading module 'kotlin-react'. Its dependency 'kotlin-kotlin-stdlib' was not found. Please, check whether 'kotlin-kotlin-stdlib' is loaded prior to 'kotlin-react'.");
    }
    if (typeof globalThis['kotlin-js'] === 'undefined') {
      throw new Error("Error loading module 'kotlin-react'. Its dependency 'kotlin-js' was not found. Please, check whether 'kotlin-js' is loaded prior to 'kotlin-react'.");
    }
    if (typeof globalThis['kotlinx-coroutines-core'] === 'undefined') {
      throw new Error("Error loading module 'kotlin-react'. Its dependency 'kotlinx-coroutines-core' was not found. Please, check whether 'kotlinx-coroutines-core' is loaded prior to 'kotlin-react'.");
    }
    globalThis['kotlin-react'] = factory(typeof globalThis['kotlin-react'] === 'undefined' ? {} : globalThis['kotlin-react'], react, globalThis['react/jsx-runtime'], globalThis['kotlin-kotlin-stdlib'], globalThis['kotlin-js'], globalThis['kotlinx-coroutines-core']);
  }
}(function (_, $module$react, $module$react_jsx_runtime_chfn3q, kotlin_kotlin, kotlin_org_jetbrains_kotlin_wrappers_kotlin_js, kotlin_org_jetbrains_kotlinx_kotlinx_coroutines_core) {
  'use strict';
  //region block: imports
  var Fragment = $module$react.Fragment;
  var jsx = $module$react_jsx_runtime_chfn3q.jsx;
  var jsxs = $module$react_jsx_runtime_chfn3q.jsxs;
  var useEffect = $module$react.useEffect;
  var Unit_instance = kotlin_kotlin.$_$.j;
  var isArray = kotlin_kotlin.$_$.w3;
  var IsolatedCoroutineScope = kotlin_org_jetbrains_kotlin_wrappers_kotlin_js.$_$.b;
  var CoroutineStart_UNDISPATCHED_getInstance = kotlin_org_jetbrains_kotlinx_kotlinx_coroutines_core.$_$.a;
  var VOID = kotlin_kotlin.$_$.a;
  var launch = kotlin_org_jetbrains_kotlinx_kotlinx_coroutines_core.$_$.g;
  var constructCallableReference = kotlin_kotlin.$_$.l3;
  var IllegalStateException_init_$Create$ = kotlin_kotlin.$_$.x;
  var singleOrNull = kotlin_kotlin.$_$.e2;
  //endregion
  //region block: pre-declaration
  //endregion
  function get_BUILDER_CHILDREN() {
    _init_properties_ChildrenBuilder_kt__gexuom();
    return BUILDER_CHILDREN;
  }
  var BUILDER_CHILDREN;
  function get_DEFAULT_KEY() {
    _init_properties_ChildrenBuilder_kt__gexuom();
    return DEFAULT_KEY;
  }
  var DEFAULT_KEY;
  function addChildNode(_this__u8e3s4, node) {
    _init_properties_ChildrenBuilder_kt__gexuom();
    // Inline function 'react.builderChildren' call
    // Inline function 'kotlin.js.asDynamic' call
    var children = _this__u8e3s4[get_BUILDER_CHILDREN()];
    if (!(children == null)) {
      children.push(node);
    } else {
      // Inline function 'react.ReactNodeArray' call
      // Inline function 'kotlin.arrayOf' call
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      // Inline function 'js.reflect.unsafeCast' call
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      // Inline function 'react.builderChildren' call
      var value = [node];
      // Inline function 'kotlin.js.asDynamic' call
      _this__u8e3s4[get_BUILDER_CHILDREN()] = value;
    }
  }
  function addChild(_this__u8e3s4, type) {
    _init_properties_ChildrenBuilder_kt__gexuom();
    addChildElement(_this__u8e3s4, type, null, getDefaultKey(_this__u8e3s4));
  }
  function addChild_0(_this__u8e3s4, type, block) {
    _init_properties_ChildrenBuilder_kt__gexuom();
    var defaultKey = getDefaultKey(_this__u8e3s4);
    // Inline function 'js.objects.unsafeJso' call
    // Inline function 'js.objects.unsafeJso' call
    // Inline function 'kotlin.apply' call
    var this_0 = {};
    block(this_0);
    addChildElement(_this__u8e3s4, type, this_0, defaultKey);
  }
  function addChildElement(_this__u8e3s4, type, props, defaultKey) {
    _init_properties_ChildrenBuilder_kt__gexuom();
    var element = jsx_0(type, props, defaultKey);
    addChildNode(_this__u8e3s4, element);
  }
  function getDefaultKey(_this__u8e3s4) {
    _init_properties_ChildrenBuilder_kt__gexuom();
    // Inline function 'kotlin.js.asDynamic' call
    var key = _this__u8e3s4[get_DEFAULT_KEY()];
    Reflect.deleteProperty(_this__u8e3s4, get_DEFAULT_KEY());
    return key;
  }
  function buildChildren(_this__u8e3s4) {
    _init_properties_ChildrenBuilder_kt__gexuom();
    return buildChildren_0(_this__u8e3s4);
  }
  function buildChildren_0(builder) {
    _init_properties_ChildrenBuilder_kt__gexuom();
    // Inline function 'kotlin.js.asDynamic' call
    var children = builder[get_BUILDER_CHILDREN()];
    Reflect.deleteProperty(builder, get_BUILDER_CHILDREN());
    return children;
  }
  var properties_initialized_ChildrenBuilder_kt_gby2z0;
  function _init_properties_ChildrenBuilder_kt__gexuom() {
    if (!properties_initialized_ChildrenBuilder_kt_gby2z0) {
      properties_initialized_ChildrenBuilder_kt_gby2z0 = true;
      BUILDER_CHILDREN = Symbol('@@builder-children');
      DEFAULT_KEY = Symbol('@@default-key');
    }
  }
  function FC(block) {
    // Inline function 'js.reflect.unsafeCast' call
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    return FC$lambda(block);
  }
  function FC_0(block) {
    // Inline function 'js.reflect.unsafeCast' call
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    return FC$lambda_0(block);
  }
  function FC$lambda($block) {
    return function () {
      return createElementOrNull($block);
    };
  }
  function FC$lambda$lambda($block, $props) {
    return function ($this$createElementOrNull) {
      $block($this$createElementOrNull, $props);
      return Unit_instance;
    };
  }
  function FC$lambda_0($block) {
    return function (props) {
      return createElementOrNull(FC$lambda$lambda($block, props));
    };
  }
  function asNodeArrayOrNull(_this__u8e3s4) {
    // Inline function 'js.reflect.upcast' call
    var tmp = _this__u8e3s4;
    return isArray(tmp) ? tmp : null;
  }
  function create(_this__u8e3s4, block) {
    // Inline function 'js.objects.unsafeJso' call
    // Inline function 'js.objects.unsafeJso' call
    // Inline function 'kotlin.apply' call
    var this_0 = {};
    block(this_0);
    return jsx_1(_this__u8e3s4, this_0);
  }
  function createElementOrNull(block) {
    // Inline function 'kotlin.takeIf' call
    var this_0 = create(Fragment, block);
    var tmp;
    if (!(this_0.props.children == null)) {
      tmp = this_0;
    } else {
      tmp = null;
    }
    return tmp;
  }
  function createCleanupCallback(block) {
    return createCleanupCallback$lambda(block);
  }
  function createCleanupCallback$lambda($block) {
    return function () {
      return runIsolatedJob($block);
    };
  }
  function runIsolatedJob(block) {
    var job = launch(IsolatedCoroutineScope(), VOID, CoroutineStart_UNDISPATCHED_getInstance(), block);
    return Job$cancel$ref(job);
  }
  function Job$cancel$ref(p0) {
    return constructCallableReference(function () {
      p0.df();
      return Unit_instance;
    }, 0, 0, 1, VOID, [p0]);
  }
  function jsx_0(type, props, defaultKey) {
    if (props == null) {
      var tmp = jsx;
      // Inline function 'js.objects.unsafeJso' call
      var tmp_0 = {};
      return tmp(type, tmp_0, defaultKey == null ? undefined : defaultKey);
    }
    var finalProps = props;
    var tmp1_elvis_lhs = props.key;
    var tmp2_elvis_lhs = tmp1_elvis_lhs == null ? defaultKey : tmp1_elvis_lhs;
    var finalKey = tmp2_elvis_lhs == null ? undefined : tmp2_elvis_lhs;
    var builderChildren = buildChildren(props);
    var jsxMode = true;
    if (!(props.key === undefined) || !(builderChildren == null)) {
      var tmp_1 = Object;
      // Inline function 'js.objects.unsafeJso' call
      var tmp$ret$1 = {};
      finalProps = tmp_1.assign(tmp$ret$1, props);
      Reflect.deleteProperty(finalProps, 'key');
      if (!(builderChildren == null)) {
        // Inline function 'js.reflect.unsafeCast' call
        // Inline function 'kotlin.js.unsafeCast' call
        // Inline function 'kotlin.js.asDynamic' call
        var container = finalProps;
        if (!(container.children == null)) {
          reportError(IllegalStateException_init_$Create$('Both `children` source options used. Prop `children` will be ignored.'));
        }
        var tmp3_safe_receiver = asNodeArrayOrNull(builderChildren);
        var singleBuilderChild = tmp3_safe_receiver == null ? null : singleOrNull(tmp3_safe_receiver);
        jsxMode = !(singleBuilderChild == null);
        container.children = singleBuilderChild == null ? builderChildren : singleBuilderChild;
      }
    }
    var create = jsxMode ? jsx : jsxs;
    return create(type, finalProps, finalKey);
  }
  function jsx_1(type, props) {
    return jsx_0(type, props, null);
  }
  function jsx_2(type) {
    var tmp = jsx;
    // Inline function 'js.objects.unsafeJso' call
    return tmp(type, {});
  }
  function invoke(_this__u8e3s4, effect, dependencies) {
    var callback = createCleanupCallback(effect);
    _this__u8e3s4(callback, dependencies);
  }
  function useEffectOnce(effect) {
    var tmp = useEffect;
    // Inline function 'kotlin.emptyArray' call
    var tmp$ret$0 = [];
    invoke(tmp, effect, tmp$ret$0);
  }
  function useEffect_0(dependencies, effect) {
    invoke(useEffect, effect, dependencies);
  }
  //region block: exports
  _.$_$ = _.$_$ || {};
  _.$_$.a = jsx_2;
  _.$_$.b = FC;
  _.$_$.c = FC_0;
  _.$_$.d = addChildNode;
  _.$_$.e = addChild;
  _.$_$.f = addChild_0;
  _.$_$.g = create;
  _.$_$.h = useEffectOnce;
  _.$_$.i = useEffect_0;
  //endregion
  return _;
}));

//# sourceMappingURL=kotlin-react.js.map
