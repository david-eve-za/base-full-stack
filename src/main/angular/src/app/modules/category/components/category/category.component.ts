import {Component, OnInit, ViewChild} from '@angular/core';
import {CategoryService} from "../../../shared/services/category.service";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";

export interface CategoryElement {
  id: number;
  name: string;
  description: string;
}

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css']
})
export class CategoryComponent implements OnInit {
  displayedColumns: string[] = ['id', 'name', 'description', 'actions'];
  dataSource = new MatTableDataSource<CategoryElement>();

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  constructor(private categoryService: CategoryService) {
  }

  ngOnInit(): void {
    this.getCategories();
  }

  getCategories() {
    this.categoryService.getCategories().subscribe(data => {
      console.log(data);
      this.processCategoryData(data);
    }, error => {
      console.log(error);
    });
  }

  processCategoryData(data: any) {
    const categories: CategoryElement[] = [];
    if (data.metadata[0].code == "200") {
      data.data.forEach((category: any) => {
        categories.push({
          id: category.id,
          name: category.name,
          description: category.description
        });
      });
      this.dataSource = new MatTableDataSource<CategoryElement>(categories);
      this.dataSource.paginator = this.paginator;
    }
  }

}
