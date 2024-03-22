/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2019-Present Datadog, Inc.
 */

import Foundation

/// `PerformancePresetOverride` is a public structure that allows you to customize
/// performance presets by setting optional limits. If the limits are not provided, the default values from
/// the `PerformancePreset` object will be used.
public struct PerformancePresetOverride {
    /// Overrides the the maximum allowed file size in bytes.
    /// If not provided, the default value from the `PerformancePreset` object is used.
    let maxFileSize: UInt64?

    /// Overrides the maximum allowed object size in bytes.
    /// If not provided, the default value from the `PerformancePreset` object is used.
    let maxObjectSize: UInt64?

    /// Overrides the maximum age qualifying given file for reuse (in seconds).
    /// If recently used file is younger than this, it is reused - otherwise: new file is created.
    let maxFileAgeForWrite: TimeInterval?

    /// Minimum age qualifying given file for upload (in seconds).
    /// If the file is older than this, it is uploaded (and then deleted if upload succeeded).
    /// It has an arbitrary offset  (~0.5s) over `maxFileAgeForWrite` to ensure that no upload can start for the file being currently written.
    let minFileAgeForRead: TimeInterval?

    /// Overrides the initial upload delay (in seconds).
    /// At runtime, the upload interval starts with `initialUploadDelay` and then ranges from `minUploadDelay` to `maxUploadDelay` depending
    /// on delivery success or failure.
    let initialUploadDelay: TimeInterval?

    /// Overrides the mininum  interval of data upload (in seconds).
    let minUploadDelay: TimeInterval?

    /// Overrides the maximum interval of data upload (in seconds).
    let maxUploadDelay: TimeInterval?

    /// Overrides the current interval is change on successful upload. Should be less or equal `1.0`.
    /// E.g: if rate is `0.1` then `delay` will be changed by `delay * 0.1`.
    let uploadDelayChangeRate: Double?

    /// Initializes a new `PerformancePresetOverride` instance with the provided overrides.
    ///
    /// - Parameters:
    ///   - maxFileSize: The maximum allowed file size in bytes, or `nil` to use the default value from `PerformancePreset`.
    ///   - maxObjectSize: The maximum allowed object size in bytes, or `nil` to use the default value from `PerformancePreset`.
    ///   - meanFileAge: The mean age qualifying a file for reuse, or `nil` to use the default value from `PerformancePreset`.
    ///   - minUploadDelay: The mininum interval of data uploads, or `nil` to use the default value from `PerformancePreset`.
    public init(
        maxFileSize: UInt64?,
        maxObjectSize: UInt64?,
        meanFileAge: TimeInterval?,
        minUploadDelay: TimeInterval?
    ) {
        self.maxFileSize = maxFileSize
        self.maxObjectSize = maxObjectSize

        if let meanFileAge {
            // Following constants are the same as in `DatadogCore.PerformancePreset`
            self.maxFileAgeForWrite = meanFileAge * 0.95 // 5% below the mean age
            self.minFileAgeForRead = meanFileAge * 1.05 //  5% above the mean age
        } else {
            self.maxFileAgeForWrite = nil
            self.minFileAgeForRead = nil
        }

        if let minUploadDelay {
            // Following constants are the same as in `DatadogCore.PerformancePreset`
            self.initialUploadDelay = minUploadDelay * 5
            self.minUploadDelay = minUploadDelay
            self.maxUploadDelay = minUploadDelay * 10
            self.uploadDelayChangeRate = 0.1
        } else {
            self.initialUploadDelay = nil
            self.minUploadDelay = nil
            self.maxUploadDelay = nil
            self.uploadDelayChangeRate = nil
        }
    }
}
