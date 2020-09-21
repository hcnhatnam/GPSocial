//
//  ButtonWithImage.swift
//  ColorNinja
//
//  Created by Do Le Duy on 5/10/20.
//  Copyright © 2020 Do Le Duy. All rights reserved.
//

import Foundation
import UIKit

class ButtonWithImage: UIView {
        
    var imageView: UIImageView!
    
    var titleLabel: UILabel!
    
    var buttonPadding: UIEdgeInsets!
    
    var spacing: CGFloat!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        imageView = UIImageView()
        titleLabel = UILabel()
        buttonPadding = UIEdgeInsets()
        spacing = 0.0
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
        
    func addTargetForTouchUpInsideEvent(target: Any, selector: Selector) {
        let gesture:UITapGestureRecognizer = UITapGestureRecognizer(target: target, action: selector)
        gesture.numberOfTapsRequired = 1
        isUserInteractionEnabled = true
        addGestureRecognizer(gesture)
    }
    
    override func layoutSubviews() {
        
        let buttonHeight: CGFloat = self.frame.size.height
        let imageViewWidth =  buttonHeight - buttonPadding.top - buttonPadding.bottom
        
        // image
        self.addSubview(imageView)
        imageView.snp.makeConstraints { (make) in
            make.width.equalTo(imageViewWidth)
            make.top.equalTo(buttonPadding.top)
            make.leading.equalTo(buttonPadding.left)
            make.bottom.equalTo(-buttonPadding.bottom)
        }
        
        // title
        self.addSubview(titleLabel)
        titleLabel.snp.makeConstraints { (make) in
            make.bottom.height.equalTo(imageView)
            make.left.equalTo(imageView.snp.right).offset(spacing)
            make.right.equalTo(-buttonPadding.right)
        }
    }
}
